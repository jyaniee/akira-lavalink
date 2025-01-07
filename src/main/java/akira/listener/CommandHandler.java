package akira.listener;

import akira.commands.*;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.player.LavalinkLoadResult;
import dev.arbjerg.lavalink.client.player.SearchResult;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.internal.LavalinkRestClient;
import dev.arbjerg.lavalink.protocol.v4.LoadResult;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import akira.music.GuildMusicManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;

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
                        Commands.slash("들어가기", "음성 채널에 들어갑니다."),
                        Commands.slash("나가기", "음성 채널에서 나갑니다."),
                        Commands.slash("정지", "현재 트랙을 정지합니다."),
                        Commands.slash("일시정지", "플레이어를 일시정지 또는 해제 합니다."),
                        Commands.slash("현재곡", "현재 재생 중인 음악을 보여줍니다."),
                        Commands.slash("재생", "음악을 재생합니다.")
                                .addOptions(
                                        new OptionData(OptionType.STRING, "플랫폼", "검색하려는 플랫폼을 선택하세요.", true)
                                                .addChoice("Youtube", "ytsearch")
                                                .addChoice("Spotify", "spsearch"),
                                        new OptionData(OptionType.STRING, "쿼리", "검색할 음악 제목/URI를 입력하세요.", true)
                                                .setAutoComplete(true)

                                ),
                        Commands.slash("재한바보", "어디 한 번 해보쇼."),
                        Commands.slash("안녕", "간단한 인사를 합니다."),
                        Commands.slash("대기열", "현재 대기열을 표시합니다."),
                        Commands.slash("볼륨", "음악 볼륨을 조정합니다.")
                                .addOption(OptionType.INTEGER, "볼륨", "설정할 볼륨 값 (0 ~ 100)", true),
                        Commands.slash("스킵", "현재 재생 중인 곡을 스킵합니다.")

                     //   Commands.slash("lava-search", "고급 검색 기능을 사용합니다.")
                     //           .addOption(OptionType.STRING, "query", "검색할 음악 제목", true)
                )
                .queue();
    }
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getFullCommandName()){
            case "들어가기" -> new Join(this).execute(event);
            case "나가기" -> new Leave().execute(event);
            case "정지" -> new Stop(client, this).execute(event);
            case "재생" -> new Play(client, this).execute(event);
            case "일시정지" -> new Pause(client).execute(event);
            case "현재곡" -> new NowPlaying(client).execute(event);
          //  case "lava-search" -> new LavaSearchCommand(client).execute(event);
            case "재한바보" -> new Babo().execute(event);
            case "안녕" -> new Hello().sayHello(event);
            case "대기열" -> new Queue(client, this).execute(event);
            case "볼륨" -> new Volume(client, this).execute(event);
            case "스킵" -> new Skip(client, this).execute(event);
        }
    }
    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if(event.getFocusedOption().getName().equals("쿼리")){
            String platform = event.getOption("플랫폼").getAsString();
            String query = event.getFocusedOption().getValue(); // 사용자가 입력 중인 텍스트

            LavalinkRestClient restClient = client.getNodes().get(0).getRest$lavalink_client();
            String searchQuery = platform + ":" + query;

            // Lavalink 검색
            restClient.loadItem(searchQuery).subscribe(LavalinkLoadResult -> {
                //if(loadResult instanceof SearchResult result){
                //if(loadResult.getClass().getSimpleName().equals("SearchResult")){
                    //List<Track> tracks = result.getTracks();
                    //List<Track> tracks = ((dev.arbjerg.lavalink.protocol.v4.LoadResult.SearchResult) loadResult).getTracks();
                if(LavalinkLoadResult instanceof LoadResult.SearchResult result){
                    List<dev.arbjerg.lavalink.protocol.v4.Track> tracks = result.getData().getTracks();
                    List<Command.Choice> choices = tracks.stream()
                            .limit(10)
                            .map(track -> new Command.Choice(track.getInfo().getTitle(), track.getInfo().getUri()))
                            .toList();
                    event.replyChoices(choices).queue();
                }else {
                    event.replyChoices().queue();
                }
            });
            return;
        }
    }

    public GuildMusicManager getOrCreateMusicManager(long guildId) {
        return this.musicManagers.computeIfAbsent(guildId, id -> new GuildMusicManager(id, client));
    }

}
