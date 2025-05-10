package akira.riot.dto;

public class SummonerDto {
    public String id;
    public String accountId;
    public String puuid;
    public int profileIconId;
    public long revisionDate;
    public long summonerLevel;

    @Override
    public String toString() {
        return "Summoner [Level " + summonerLevel + ", PUUID: " + puuid + "]";
    }
}
