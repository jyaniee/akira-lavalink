package akira;

import akira.listener.EventListener;
import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.JDA;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Dotenv dotenv = Dotenv.load();

        String botToken = dotenv.get("BOT_TOKEN");

        if(botToken == null || botToken.isEmpty()) {
            LOG.error("봇 토큰이 설정되지 않았습니다. BOT_TOKEN 환경 변수를 확인하세요.");
            return;
        }

        LavalinkClient client = new LavalinkClient(
                Helpers.getUserIdFromToken(botToken)
        );

        JDABuilder.createDefault(botToken)
                .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(client))

                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new EventListener(client))
                .build()
                .awaitReady();

        LOG.info("봇이 성공적으로 구동되었습니다!");
    }

}
