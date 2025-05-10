package akira.riot.commands;

import akira.riot.RiotService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class MatchHistory {
    private final RiotService riotService;

    public MatchHistory(RiotService riotService) {
        this.riotService = riotService;
    }

    private String getRankImageUrl(String tier) {
        if (tier == null || tier.isBlank() || tier.equalsIgnoreCase("Unranked")) {
            return "https://opgg-static.akamaized.net/images/medals_new/unranked.png?image=q_auto:good,f_webp,w_144";
        }

        String lowerTier = tier.trim().toLowerCase().split(" ")[0]; // e.g. "Platinum III" → "platinum"
        return String.format("https://opgg-static.akamaized.net/images/medals_new/%s.png?image=q_auto:good,f_webp,w_144", lowerTier);
    }

    private String getProfileIconUrl(int iconId) {
        return String.format("https://ddragon.leagueoflegends.com/cdn/14.9.1/img/profileicon/%d.png", iconId);
    }

    private int calcWinRate(int wins, int losses) {
        int total = wins + losses;
        return total == 0 ? 0 : (int)((wins * 100.0) / total);
    }

    public void execute(SlashCommandInteractionEvent event) {
        String gameName = event.getOption("이름").getAsString();
        String tagLine = event.getOption("태그").getAsString();

        event.deferReply().queue();

        try{
            RiotService.PlayerInfo info = riotService.getPlayerBasicInfo(gameName, tagLine);

            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(gameName + "#" + tagLine + "의 전적 정보", null, getProfileIconUrl(info.profileIconId()))
                    .setColor(Color.MAGENTA)
                    .setDescription(
                            String.format(
                                    "레벨: %d\n\n" +
                                            "**솔로랭크**\n" +
                                            "%s %dLP\n%dW %dL | 승률 %d%%\n\n" +
                                            "**자유랭크**\n" +
                                            "%s %dLP\n%dW %dL | 승률 %d%%",
                                    info.level(),
                                    info.soloRank(), info.soloLp(), info.soloWins(), info.soloLosses(),
                                    calcWinRate(info.soloWins(), info.soloLosses()),
                                    info.flexRank(), info.flexLp(), info.flexWins(), info.flexLosses(),
                                    calcWinRate(info.flexWins(), info.flexLosses())
                            )
                    )
                    .setThumbnail(getRankImageUrl(info.soloRank()))
                    .setFooter("Powered by Riot Games API", null);
            event.getHook().sendMessageEmbeds(embed.build()).queue();

        }catch (Exception e){
            EmbedBuilder errorEmbed = new EmbedBuilder()
                    .setTitle("❗ 전적 조회 실패")
                    .setDescription("전적 정보를 불러오는 중 오류가 발생했습니다.\nRiot ID가 존재하지 않거나 API 오류일 수 있습니다.")
                    .setColor(Color.RED);

            event.getHook().sendMessageEmbeds(errorEmbed.build()).queue();
            e.printStackTrace(); // 콘솔 디버깅용
        }


    }
}
