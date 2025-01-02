package akira.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MusicManager {
    private final Link link;

    public MusicManager(LavalinkClient client, net.dv8tion.jda.api.entities.Guild guild) {
        this.link = client.getOrCreateLink(guild.getIdLong());
    }

    public void playTrack(String trackUrl, MessageReceivedEvent event) {
        var voiceChannel = event.getMember().getVoiceState().getChannel();

        if(voiceChannel == null) {
            event.getChannel().sendMessage("먼저 음성 채널에 참가하세요!").queue();
            return;
        }

        event.getJDA().getDirectAudioController().connect(voiceChannel);

        link.loadItem(trackUrl).subscribe(player ->{
            event.getChannel().sendMessage("재생 중: " + trackUrl).queue();
        });
    }

    public void stopTrack(MessageReceivedEvent event) {
        var guild = event.getGuild();

        if(guild == null) {
            event.getChannel().sendMessage("길드 정보를 찾을 수 없습니다.").queue();
            return;
        }

        event.getJDA().getDirectAudioController().disconnect(guild);

        event.getChannel().sendMessage("음악을 정지하고 음성 채널에서 나갑니다!").queue();
    }
}
