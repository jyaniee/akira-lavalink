package akira.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;

public class ChampionNameMapper {
    private static final String URL =
            "https://ddragon.leagueoflegends.com/cdn/15.9.1/data/ko_KR/champion.json";

    private static final Map<Integer, String> idToNameMap = new HashMap<>();

    static {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
            connection.setRequestMethod("GET");

            JsonObject root = JsonParser.parseReader(new InputStreamReader(connection.getInputStream()))
                    .getAsJsonObject()
                    .getAsJsonObject("data");

            for (String champKey : root.keySet()) {
                JsonObject champ = root.getAsJsonObject(champKey);
                int champId = Integer.parseInt(champ.get("key").getAsString()); // "key": "39"
                String champName = champ.get("name").getAsString(); // "name": "이렐리아"
                idToNameMap.put(champId, champName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getChampionName(int championId) {
        return idToNameMap.getOrDefault(championId, "알 수 없음");
    }
}
