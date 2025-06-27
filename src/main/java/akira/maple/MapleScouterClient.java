package akira.maple;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MapleScouterClient {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_URL = "https://api.maplescouter.com/api/id";
    private static final String API_KEY =  dotenv.get("MAPLESCOUTER_API_KEY");

    private final OkHttpClient client;
    private final Gson gson;

    public MapleScouterClient() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    public JsonObject[] fetchCharacterInfo(String nickname) {
        String encodedName = URLEncoder.encode(nickname, StandardCharsets.UTF_8);
        HttpUrl url = HttpUrl.parse(API_URL).newBuilder()
                .addQueryParameter("name", encodedName)
                .addQueryParameter("preset", "00000")
                .addQueryParameter("region", "kms")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("api-key", API_KEY)
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("Accept", "application/json")
                .addHeader("Origin", "https://maplescouter.com")
                .addHeader("Referer", "https://maplescouter.com/")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("[ERROR] 응답 실패: " + response.code());
                return null;
            }
            String responseBody = response.body().string();
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);

            JsonObject userApiData = json.getAsJsonObject("userApiData");
            JsonObject info = userApiData.getAsJsonObject("info");

            JsonObject calculated = json.getAsJsonObject("calculatedData");

            return new JsonObject[]{info, calculated};

        } catch (IOException | IllegalStateException e) {
            System.err.println("[ERROR] API 요청 중 오류 발생: " + e.getMessage());
            return null;
        }
    }
}
