package akira;

import akira.listener.CommandHandler;
import dev.arbjerg.lavalink.client.event.*;
import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup;

import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static CommandHandler listener;
    private static final int SESSION_INVALID = 4006;

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

        client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());

        registerLavalinkListeners(client);
        registerLavalinkNodes(client);

        listener = new CommandHandler(client);

        final var jda = JDABuilder.createDefault(botToken)
                .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(client))

                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES)
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(
                    listener
                )
                .build()
                .awaitReady();



        client.on(WebSocketClosedEvent.class).subscribe((event) -> {
            if(event.getCode() == SESSION_INVALID){
                final var guildId = event.getGuildId();
                final var guild = jda.getGuildById(guildId);

                if(guild == null) {
                    return;
                }

                final var connectedChannel = guild.getSelfMember().getVoiceState().getChannel();

                if(connectedChannel == null) {
                    return;
                }

                jda.getDirectAudioController().reconnect(connectedChannel);
            }
        });

    }

    private static void registerLavalinkNodes(LavalinkClient client) {
        List.of(
//            client.addNode(
//                new NodeOptions.Builder()
//                    .setName("localhost")
//                    .setServerUri("ws://localhost")
//                    .setPassword("youshallnotpass")
//                    .build()
//            )
//            client.addNode(
//                new NodeOptions.Builder()
//                    .setName("optiplex")
//                    .setServerUri("ws://optiplex.local.duncte123.lgbt")
//                    .setPassword("youshallnotpass")
//                    .build()
//            ),
//
                client.addNode(
                        new NodeOptions.Builder()
                                .setName("localhost")
                                .setServerUri("ws://localhost")
                                .setPassword("0507")
                                .setRegionFilter(RegionGroup.ASIA)
                                .build()
                )
        ).forEach((node) -> {
            node.on(TrackStartEvent.class).subscribe((event) -> {
                final LavalinkNode node1 = event.getNode();

                LOG.trace(
                        "{}: 플레이리스트 재생 시작: {}",
                        node1.getName(),
                        event.getTrack().getInfo()
                );
            });
        });
    }

    private static void registerLavalinkListeners(LavalinkClient client) {
        client.on(ReadyEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            LOG.info(
                    "노드 '{}' 준비 완료, 세션 ID: '{}'!",
                    node.getName(),
                    event.getSessionId()
            );
        });

        client.on(StatsEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            LOG.info(
                    "노드 '{}' 통계: 현재 프레이어 {}/{} (링크 수: {})",
                    node.getName(),
                    event.getPlayingPlayers(),
                    event.getPlayers(),
                    client.getLinks().size()
            );
        });

        client.on(TrackStartEvent.class).subscribe((event) -> {
            Optional.ofNullable(listener.musicManagers.get(event.getGuildId())).ifPresent(
                    (mng) -> mng.scheduler.onTrackStart(event.getTrack())
            );
        });

        client.on(TrackEndEvent.class).subscribe((event) -> {
            Optional.ofNullable(listener.musicManagers.get(event.getGuildId())).ifPresent(
                    (mng) -> mng.scheduler.onTrackEnd(event.getTrack(), event.getEndReason())
            );
        });

        client.on(EmittedEvent.class).subscribe((event) -> {
            if (event instanceof TrackStartEvent) {
                LOG.info("트랙 시작 이벤트 발생!");
            }

            final var node = event.getNode();

            LOG.info(
                    "노드 '{}'에서 이벤트 발생: {}",
                    node.getName(),
                    event
            );
        });
    }

}
