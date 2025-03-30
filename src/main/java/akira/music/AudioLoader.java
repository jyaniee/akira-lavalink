package akira.music;

import dev.arbjerg.lavalink.client.AbstractAudioLoadResultHandler;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.*;
import akira.MyUserData;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import okhttp3.Address;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AudioLoader extends AbstractAudioLoadResultHandler {
    private final SlashCommandInteractionEvent event;
    private final GuildMusicManager mngr;
    private final boolean silent;
    private final Runnable onTrackLoadedCallback;
    public AudioLoader(SlashCommandInteractionEvent event, GuildMusicManager mngr, boolean silent, Runnable onTrackLoadedCallback) {
        this.event = event;
        this.mngr = mngr;
        this.silent = silent;
        this.onTrackLoadedCallback = onTrackLoadedCallback;
    }
    public AudioLoader(SlashCommandInteractionEvent event, GuildMusicManager mngr) {
        this(event, mngr, false, null);
    }

    @Override
    public void ontrackLoaded(@NotNull TrackLoaded result) {
        final Track track = result.getTrack();
        var userData = new MyUserData(event.getUser().getIdLong());
        track.setUserData(userData);
        System.out.println("[AudioLoader] Adding track to queue: " + track.getInfo().getTitle());
        this.mngr.scheduler.enqueue(track);

        final var trackTitle = track.getInfo().getTitle();

        if(!silent) {
            event.getHook().sendMessage("대기열에 추가되었습니다: " + trackTitle + "\n요청자: <@" + userData.requester() + '>').queue();
        }
        if(onTrackLoadedCallback != null) {
            onTrackLoadedCallback.run();
        }
    }

    @Override
    public void onPlaylistLoaded(@NotNull PlaylistLoaded result) {
        final int trackCount = result.getTracks().size();
        this.mngr.scheduler.enqueuePlaylist(result.getTracks());
        if(!silent) {
            event.getHook()
                    .sendMessage(result.getInfo().getName() + "에서 " + trackCount + "개의 트랙이 대기열에 추가되었습니다!")
                    .queue();
        }
        if(onTrackLoadedCallback != null) {
            onTrackLoadedCallback.run();
        }
    }

    @Override
    public void onSearchResultLoaded(@NotNull SearchResult result) {
        final List<Track> tracks = result.getTracks();

        if (tracks.isEmpty()) {
            event.getHook().sendMessage("트랙을 찾을 수 없습니다!").queue();
            return;
        }

        final Track firstTrack = tracks.get(0);

        this.mngr.scheduler.enqueue(firstTrack);
        if(!silent) {
            event.getHook().sendMessage("대기열에 추가되었습니다: " + firstTrack.getInfo().getTitle()).queue();
        }

        if(onTrackLoadedCallback != null) {
            onTrackLoadedCallback.run();
        }
    }

    @Override
    public void noMatches() {
        if(!silent) {
            event.getHook().sendMessage("입력한 내용과 일치하는 항목이 없습니다!").queue();
        }
        if(onTrackLoadedCallback != null) {
            onTrackLoadedCallback.run();
        }

    }

    @Override
    public void loadFailed(@NotNull LoadFailed result) {
        if(!silent) {
            event.getHook().sendMessage("트랙을 불러오지 못했습니다! 오류: " + result.getException().getMessage()).queue();
        }
        if(onTrackLoadedCallback != null) {
            onTrackLoadedCallback.run();
        }
    }
}
