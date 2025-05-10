package akira.riot.dto;

public class AccountDto {
    public String puuid;
    public String gameName;
    public String tagLine;

    @Override
    public String toString() {
        return gameName + "#" + tagLine + " (" + puuid + ")";
    }
}
