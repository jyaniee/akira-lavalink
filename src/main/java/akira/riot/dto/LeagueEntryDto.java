package akira.riot.dto;

public class LeagueEntryDto {
    public String leagueId;
    public String queueType;
    public String tier;
    public String rank;
    public String summonerId;
    public String puuid;
    public int leaguePoints;
    public int wins;
    public int losses;
    public boolean veteran;
    public boolean inactive;
    public boolean firstBlood;
    public boolean hotStreak;

    @Override
    public String toString() {
        return "[%s] %s %s (%d LP) - %d승 %d패 | 연승 중: %s".formatted(
                queueType,
                tier,
                rank,
                leaguePoints,
                wins,
                losses,
                hotStreak ? "예" : "아니오"
        );
    }
}
