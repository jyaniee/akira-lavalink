package akira.maple.commands;

import akira.maple.MapleApiClient;
import akira.maple.dto.CharacterBasicDto;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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

    public void execute(SlashCommandInteractionEvent event) {
        String nickname = event.getOption("닉네임").getAsString();
        event.deferReply().queue();

        try {
            String ocid = client.getOcidByName(nickname);

            List<String> logs = new ArrayList<>();
            long[] expList = new long[7];
            int level = -1;
            double lastExpRate = 0;

            for (int i = 6; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i + 1); // 오늘 제외, 총 7일치
                CharacterBasicDto data = client.getCharacterBasic(ocid, date.toString());

                long exp = data.getCharacterExp();
                expList[6 - i] = exp;

                double expRate = Double.parseDouble(data.getCharacterExpRate());
                String log = formatter.format(date) + " : Lv." + data.getCharacterLevel() +
                        String.format(" %.3f%%", expRate);

                if (6 - i > 0) {
                    long diff = exp - expList[6 - i - 1];
                    log += String.format(" (+%,.1f조)", diff / 1_0000_0000_0000.0);
                }

                logs.add(log);
                level = data.getCharacterLevel();
                lastExpRate = Double.parseDouble(data.getCharacterExpRate());
            }

            // 평균 경험치 계산
            long totalGain = expList[6] - expList[0];
            double dailyAvg = totalGain / 7.0;
            long expLeft = (long) ((100.0 - lastExpRate) / 100.0 * 250_000_000_000.0); // 대략적인 291 기준 경험치량
            int daysLeft = (int) Math.ceil(expLeft / dailyAvg);

            LocalDate estimatedDate = LocalDate.now().plusDays(daysLeft);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("[" + nickname + "] - 경험치 히스토리");
            embed.setColor(Color.ORANGE);
            for (String log : logs) {
                embed.addField("", log, false);
            }

            embed.addBlankField(false);
            embed.addField("일일 평균 획득량", String.format("%.1f조", dailyAvg / 1_0000_0000_0000.0), false);
            embed.addField("남은 경험치량", String.format("%.1f조", expLeft / 1_0000_0000_0000.0), false);
            embed.addField("예상 레벨업 날짜", estimatedDate + " (" + daysLeft + "일 후)", false);
            embed.setFooter("개발 중입니다.");

            event.getHook().sendMessageEmbeds(embed.build()).queue();

        } catch (IOException e) {
            event.getHook().sendMessage("❌ 데이터를 불러오는 중 오류가 발생했습니다: " + e.getMessage()).queue();
        }
    }
}
