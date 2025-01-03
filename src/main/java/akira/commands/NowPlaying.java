package akira.commands;

import akira.MyUserData;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class NowPlaying {
    private final LavalinkClient client;

    public NowPlaying(LavalinkClient client) {
        this.client = client;
    }

    public void execute(SlashCommandInteractionEvent event){
        Guild guild = event.getGuild();
        final var link = this.client.getOrCreateLink(guild.getIdLong());
        final var player = link.getCachedPlayer();

        if(player == null){
            event.reply("채널에 연결되어있지 않거나 플레이어가 존재하지 않습니다.").queue();
            return;
        }
        final var track = player.getTrack();

        if(track == null){
            event.reply("재생 중인 음악이 없습니다!").queue();
            return;
        }

        final var trackInfo = track.getInfo();;

        event.reply(
                "현재 재생 중: %s\n재생 시간: %s/%s\n요청자: <@%s>".formatted(
                        trackInfo.getTitle(),
                        player.getPosition(),
                        trackInfo.getLength(),
                        track.getUserData(MyUserData.class).requester()
                )
        ).queue();
    }
}
