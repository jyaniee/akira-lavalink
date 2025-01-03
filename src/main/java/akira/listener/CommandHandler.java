package akira.listener;

import akira.commands.*;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import akira.music.GuildMusicManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandHandler extends ListenerAdapter {
    private final LavalinkClient client;
    public final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(CommandHandler.class);

    public CommandHandler(LavalinkClient client) {
        this.client = client;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event){
        LOG.info("{}가 준비되었습니다!", event.getJDA().getSelfUser().getAsTag());

        event.getJDA().updateCommands()
                .addCommands(
                        Commands.slash("join", "음성 채널에 참가합니다."),
                        Commands.slash("leave", "음성 채널에서 나갑니다."),
                        Commands.slash("stop", "현재 트랙을 정지합니다."),
                        Commands.slash("pause", "플레이어를 일시정지 또는 해제 합니다."),
                        Commands.slash("now-playing", "현재 재생 중인 음악을 보여줍니다."),
                        Commands.slash("play", "음악을 재생합니다.")
                                .addOption(
                                        OptionType.STRING,
                                        "제목",
                                        "재생하려는 음악의 제목",
                                        true
                                )
                )
                .queue();
    }
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getFullCommandName()){
            case "join" -> new Join().execute(event);
            case "leave" -> new Leave().execute(event);
            case "stop" -> new Stop(client).execute(event);
            case "play" -> new Play(client).execute(event);
            case "pause" -> new Pause(client).execute(event);
            case "now-playing" -> new NowPlaying(client).execute(event);
        }
    }
}
