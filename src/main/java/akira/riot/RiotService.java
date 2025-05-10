package akira.riot;

import akira.riot.dto.AccountDto;
import akira.riot.dto.ChampionMasteryDto;
import akira.riot.dto.LeagueEntryDto;
import akira.riot.dto.SummonerDto;
import akira.util.ChampionNameMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class RiotService {
    private static final Logger LOG = LoggerFactory.getLogger(RiotService.class);
    private final RiotApiClient apiClient;

    public RiotService(RiotApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public PlayerInfo getPlayerBasicInfo(String gameName, String tagLine) throws IOException {
        LOG.info("[getPlayerBasicInfo] Riot ID: {}#{}", gameName, tagLine);

        AccountDto account = apiClient.getAccountByRiotId(gameName, tagLine);
        SummonerDto summoner = apiClient.getSummonerByPuuid(account.puuid);
        List<LeagueEntryDto> ranks = apiClient.getLeagueEntriesByPuuid(account.puuid);
        ChampionMasteryDto topMastery = apiClient.getTopChampionMasteryByPuuid(account.puuid);

        Optional<LeagueEntryDto> solo = ranks.stream()
                .filter(r -> r.queueType.equals("RANKED_SOLO_5x5"))
                .findFirst();

        Optional<LeagueEntryDto> flex = ranks.stream()
                .filter(r -> r.queueType.equals("RANKED_FLEX_SR"))
                .findFirst();

        String mostChampionName = topMastery != null
                ? ChampionNameMapper.getChampionName(topMastery.championId)
                : "없음";

        int mostChampionPoints = topMastery != null ? topMastery.championPoints : 0;

        return new PlayerInfo(
                summoner.id,
                summoner.summonerLevel,
                solo.map(r -> r.tier + " " + r.rank).orElse("Unranked"),
                solo.map(r -> r.leaguePoints).orElse(0),
                solo.map(r -> r.wins).orElse(0),
                solo.map(r -> r.losses).orElse(0),
                flex.map(r -> r.tier + " " + r.rank).orElse("Unranked"),
                flex.map(r -> r.leaguePoints).orElse(0),
                flex.map(r -> r.wins).orElse(0),
                flex.map(r -> r.losses).orElse(0),
                summoner.profileIconId,
                mostChampionName,
                mostChampionPoints

        );
    }

    public record PlayerInfo(
            String summonerId,
            long level,
            String soloRank,     // "Diamond III"
            int soloLp,
            int soloWins,
            int soloLosses,
            String flexRank,     // "Platinum II"
            int flexLp,
            int flexWins,
            int flexLosses,
            int profileIconId,
            String mostChampionName,
            int mostChampionPoints

    ) {}
}
