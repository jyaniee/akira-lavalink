package akira.maple;

import akira.maple.dto.AccountDto;
import akira.maple.dto.CharacterBasicDto;
import akira.riot.RiotApiClient;
import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapleApiClient {

    private static final Dotenv dotenv = Dotenv.load();
    private static final Logger LOG = LoggerFactory.getLogger(MapleApiClient.class);
    private static final String MAPLE_API_KEY = dotenv.get("MAPLE_API_KEY");
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public MapleApiClient() {
        if (MAPLE_API_KEY == null || MAPLE_API_KEY.isBlank()) {
            LOG.error("MAPLE_API_KEY 환경변수가 설정되지 않았습니다.");
            throw new IllegalStateException("Maple API 키가 누락되었습니다.");
        }
    }

    // 닉네임 -> ocid
    public String getOcidByName(String characterName) throws IOException {
        String url = "https://open.api.nexon.com/maplestory/v1/id?character_name=" + characterName;

        LOG.info("[getOcidByName] 호출: {} → {}", characterName, url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-nxopen-api-key", MAPLE_API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            checkResponse(response);
            return gson.fromJson(response.body().charStream(), AccountDto.class).getOcid();
        }
    }

    // ocid + 날짜 → 경험치 정보
    public CharacterBasicDto getCharacterBasic(String ocid, String date) throws IOException {
        String url = String.format(
                "https://open.api.nexon.com/maplestory/v1/character/basic?ocid=%s&date=%s",
                ocid, date
        );

        LOG.info("[getCharacterBasic] 호출: ocid={} date={} → {}", ocid, date, url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-nxopen-api-key", MAPLE_API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            checkResponse(response);
            return gson.fromJson(response.body().charStream(), CharacterBasicDto.class);
        }
    }

    private void checkResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            LOG.warn("Maple API 요청 실패: {} - {}", response.code(), response.message());
            throw new IOException("Maple API 요청 실패: " + response.code() + " - " + response.message());
        }
    }
}
