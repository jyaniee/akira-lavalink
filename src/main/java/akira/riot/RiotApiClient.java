package akira.riot;

import akira.riot.dto.AccountDto;
import akira.riot.dto.LeagueEntryDto;
import akira.riot.dto.SummonerDto;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;

public class RiotApiClient {
    private static final Logger LOG = LoggerFactory.getLogger(RiotApiClient.class);
    private static final String RIOT_API_KEY = System.getenv("RIOT_TOKEN");
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public  RiotApiClient() {
        if (RIOT_API_KEY == null || RIOT_API_KEY.isBlank()) {
            LOG.error("RIOT_TOKEN 환경변수가 설정되지 않았습니다.");
            throw new IllegalStateException("Riot API 키가 누락되었습니다.");
        }
    }

    // 1. Riot ID → PUUID 조회
    public AccountDto getAccountByRiotId(String gameName, String tagLine) throws IOException {
        String url = String.format(
                "https://asia.api.riotgames.com/riot/account/v1/accounts/by-riot-id/%s/%s?api_key=%s",
        gameName, tagLine, RIOT_API_KEY
        );

        LOG.info("[getAccountByRiotId] 호출: {}#{} → {}", gameName, tagLine, url);

        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkResponse(response);
            return gson.fromJson(response.body().charStream(), AccountDto.class);
        }
    }

    // 2. PUUID → Summoner 정보 조회
    public SummonerDto getSummonerByPuuid(String puuid) throws IOException {
        String url = String.format(
                "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/%s?api_key=%s",
                puuid, RIOT_API_KEY
        );

        LOG.info("[getSummonerByPuuid] 호출: puuid={} → {}", puuid, url);

        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkResponse(response);
            return gson.fromJson(response.body().charStream(), SummonerDto.class);
        }
    }

    // 3. PUUID → 랭크 정보 리스트 조회
    public List<LeagueEntryDto> getLeagueEntriesByPuuid(String puuid) throws IOException {
        String url = String.format(
                "https://kr.api.riotgames.com/lol/league/v4/entries/by-puuid/%s?api_key=%s",
                puuid, RIOT_API_KEY
        );

        LOG.info("[getLeagueEntriesByPuuid] 호출: puuid={} → {}", puuid, url);

        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkResponse(response);
            Type listType = new TypeToken<List<LeagueEntryDto>>(){}.getType();
            return gson.fromJson(response.body().charStream(), listType);
        }
    }

    private void checkResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            LOG.warn("Riot API 요청 실패: {} - {}", response.code(), response.message());
            throw new IOException("Riot API 요청 실패: " + response.code() + " - " + response.message());
        }
    }
}
