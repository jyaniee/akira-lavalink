package akira.maple.commands;

import akira.maple.MapleApiClient;
import akira.maple.dto.CharacterBasicDto;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExpHistory extends ListenerAdapter {
    private final MapleApiClient apiClient = new MapleApiClient();
    private final MapleApiClient client = new MapleApiClient();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일");
    private static final Logger LOG = LoggerFactory.getLogger(ExpHistory.class);

    public void execute(SlashCommandInteractionEvent event) {
        String nickname = event.getOption("닉네임").getAsString();
        event.deferReply().queue();

        try {
            String ocid = client.getOcidByName(nickname);

            List<String> logs = new ArrayList<>();
            long[] expList = new long[7];
            int[] levelList = new int[7];
            double lastExpRate = 0;
            String worldName = "";

            for (int i = 0; i < 7; i++) {
                LocalDate date;
                CharacterBasicDto data;

                if (i == 6) {
                    // 오늘
                    date = LocalDate.now();
                    data = client.getCharacterBasic(ocid);
                } else {
                    date = LocalDate.now().minusDays(6 - i);
                    data = client.getCharacterBasic(ocid, date.toString());
                }

                long exp = data.getCharacterExp();
                int level = data.getCharacterLevel();

                expList[i] = exp;
                levelList[i] = level;

                double expRate = Double.parseDouble(data.getCharacterExpRate());
                String log = "**" + formatter.format(date) + " :** Lv." + data.getCharacterLevel() +
                        String.format(" %.3f%%", expRate);

                if (i > 0) {
                    long diff;
                    if(levelList[i] > levelList[i-1]) {
                        // 레벨업
                        double prevRate = Double.parseDouble(client.getCharacterBasic(ocid, date.minusDays(1).toString()).getCharacterExpRate());
                        long prevExp = expList[i - 1];

                        // 이전 레벨의 총 필요 경험치
                        long prevTotalExp = (long) (prevExp / (prevRate / 100.0));
                        long gained = (prevTotalExp - prevExp) + exp;
                        diff = gained;
                    } else {
                        diff = expList[i] - expList[i - 1];
                    }

                    String diffStr = formatExpDiff(diff);
                    log += diffStr;
                }

                logs.add(log);
                level = data.getCharacterLevel();
                lastExpRate = Double.parseDouble(data.getCharacterExpRate());
                worldName = data.getWorldName();

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    LOG.warn("쓰레드 대기 중 인터럽트 발생: {}", e.getMessage());
                    Thread.currentThread().interrupt(); // 인터럽트 상태 복원
                }
            }

            // 평균 경험치 계산

            //long totalGain = expList[6] - expList[0];

            long totalGain = 0;
            for(int i = 1; i < 7; i++) {
                long todayExp = expList[i];
                long yesterdayExp = expList[i - 1];
                int todayLevel = levelList[i];
                int yesterdayLevel = levelList[i - 1];

                if (todayLevel == yesterdayLevel) {
                    // 레벨업 X
                    totalGain += todayExp - yesterdayExp;
                } else {
                    // 레벨업 O
                    LocalDate prevDate = (i == 6) ? LocalDate.now().minusDays(1) : LocalDate.now().minusDays(6 - i + 1);
                    CharacterBasicDto prevData = client.getCharacterBasic(ocid, prevDate.toString());
                    double prevRate = Double.parseDouble(prevData.getCharacterExpRate());

                    long prevTotalExp = (long) (yesterdayExp / (prevRate / 100.0));
                    long gained = (prevTotalExp - yesterdayExp) + todayExp;

                    totalGain += gained;
                }
            }
            double dailyAvg = totalGain / 6.0;

            long currentExp = expList[6]; // 마지막 날 경험치량
            double rate = lastExpRate;

            // 경험치 역산: 총 경험치량 = 현재 경험치량 ÷ (퍼센트 / 100)
            long totalExpRequired = (long) (currentExp / (rate / 100.0));
            long expLeft = totalExpRequired - currentExp;

            // 예상 날짜 계산
            int daysLeft = (int) Math.ceil(expLeft / dailyAvg);
            LocalDate estimatedDate = LocalDate.now().plusDays(daysLeft);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("메이플 경험치 히스토리");
            embed.addField("닉네임", nickname, true);
            embed.addField("서버", worldName, true);
            embed.setColor(0x0077ff);
            for (String log : logs) {
                embed.addField("", log, false);
            }
            embed.addBlankField(false);
            embed.addField("일일 평균 획득량", formatExp((long) dailyAvg), false);
            embed.addField("남은 경험치량", formatExp(expLeft), false);
            embed.addField("예상 레벨업 날짜", estimatedDate + " (" + daysLeft + "일 후)", false);
            embed.setFooter("Data based on NEXON Open API");

            event.getHook().sendMessageEmbeds(embed.build()).queue();

        } catch (IOException e) {
            event.getHook().sendMessage("❌ 데이터를 불러오는 중 오류가 발생했습니다: " + e.getMessage()).queue();
        }
    }

    private String formatExp(long value) {
        double num = value;
        if (num >= 1_0000_0000_0000L) { // 1조
            return String.format("%.1f조", num / 1_0000_0000_0000.0);
        } else if (num >= 1_0000_0000L) { // 1억
            return String.format("%.1f억", num / 1_0000_0000.0);
        } else if (num >= 1_0000L) { // 1만
            return String.format("%.1f만", num / 1_0000.0);
        } else {
            return String.valueOf(value); // 그대로 출력
        }
    }

    private String formatExpDiff(long diff) {
        if (diff >= 1_0000_0000_0000L) return String.format(" *(+%,.1f조)*", diff / 1_0000_0000_0000.0);
        else if (diff >= 1_0000_0000L) return String.format(" *(+%,.1f억)*", diff / 1_0000_0000.0);
        else if (diff >= 1_0000L) return String.format(" *(+%,.1f만)*", diff / 1_0000.0);
        else return String.format(" *(+%,d)*", diff);
    }
}
