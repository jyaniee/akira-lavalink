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
    private final Long requesterOverrideId;
    private final boolean isExplicitJpopListRequest;

    public AudioLoader(SlashCommandInteractionEvent event, GuildMusicManager mngr, boolean silent, Runnable onTrackLoadedCallback) {
        this.event = event;
        this.mngr = mngr;
        this.silent = silent;
        this.onTrackLoadedCallback = onTrackLoadedCallback;
        this.requesterOverrideId = null;
        this.isExplicitJpopListRequest = false;
    }

    // 기본 생성자 (일반 재생 명령어)
    public AudioLoader(SlashCommandInteractionEvent event, GuildMusicManager mngr) {
        this(event, mngr, false, null, null, false);
    }
    
    // silent,  callback 생성자
    public AudioLoader(SlashCommandInteractionEvent event, GuildMusicManager mngr, boolean silent, Runnable onTrackLoadedCallback, Long requesterOverrideId) {
        this.event = event;
        this.mngr = mngr;
        this.silent = silent;
        this.onTrackLoadedCallback = onTrackLoadedCallback;
        this.requesterOverrideId = requesterOverrideId;
        this.isExplicitJpopListRequest = false;
    }
    
    // 봇 요청 여부, 사용자 지정
    public AudioLoader(SlashCommandInteractionEvent event, GuildMusicManager mngr, boolean silent, Runnable onTrackLoadedCallback, Long requesterOverrideId, boolean isExplicitJpopListRequest) {
        this.event = event;
        this.mngr = mngr;
        this.silent = silent;
        this.onTrackLoadedCallback = onTrackLoadedCallback;
        this.requesterOverrideId = requesterOverrideId;
        this.isExplicitJpopListRequest = isExplicitJpopListRequest;
    }

    private String formatRequesterText(long requesterId) {
        if (requesterOverrideId != null && isExplicitJpopListRequest) {
            return "`JPOP 리스트` (by <@" + requesterId + ">)";
        } else {
            return "<@" + requesterId + ">";
        }
    }

    @Override
    public void ontrackLoaded(@NotNull TrackLoaded result) {
        System.out.println("[AudioLoader] ontrackLoaded 호출됨");
        final Track track = result.getTrack();
        long requesterId = (requesterOverrideId != null) ? requesterOverrideId : event.getUser().getIdLong();
        // var userData = new MyUserData(event.getUser().getIdLong());
        String sourceType = (requesterOverrideId != null)
                ? (isExplicitJpopListRequest ? "jpop" : "autoplay")
                : "user";

        track.setUserData(new MyUserData(requesterId, sourceType));

        System.out.println("[AudioLoader] Adding track to queue: " + track.getInfo().getTitle());
        this.mngr.scheduler.enqueue(track);

        final var trackTitle = track.getInfo().getTitle();

        if(!silent) {
            String requesterText = formatRequesterText(requesterId);
            event.getHook().sendMessage("대기열에 추가되었습니다: " + trackTitle + "\n요청자: " + requesterText).queue();
        }
        if(onTrackLoadedCallback != null) {
            onTrackLoadedCallback.run();
        }
    }

    @Override
    public void onPlaylistLoaded(@NotNull PlaylistLoaded result) {
        System.out.println("[AudioLoader] onPlaylistLoaded 호출됨. 트랙 수: " + result.getTracks().size());
        long requesterId = (requesterOverrideId != null) ? requesterOverrideId : event.getUser().getIdLong();
        String sourceType = (requesterOverrideId != null)
                ? (isExplicitJpopListRequest ? "jpop" : "autoplay")
                : "user";

        for(Track track : result.getTracks()) {
            track.setUserData(new MyUserData(requesterId, sourceType));
        }
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
        System.out.println("[AudioLoader] onSearchResultLoaded 호출됨. 트랙 수: " + result.getTracks().size());
        final List<Track> tracks = result.getTracks();

        if (tracks.isEmpty()) {
            event.getHook().sendMessage("트랙을 찾을 수 없습니다!").queue();
            return;
        }

        final Track firstTrack = tracks.get(0);

        long requesterId = (requesterOverrideId != null) ? requesterOverrideId : event.getUser().getIdLong();
        String sourceType = (requesterOverrideId != null)
                ? (isExplicitJpopListRequest ? "jpop" : "autoplay")
                : "user";

        firstTrack.setUserData(new MyUserData(requesterId, sourceType));
        this.mngr.scheduler.enqueue(firstTrack);


        if (!silent) {
            String requesterText = formatRequesterText(requesterId);
            event.getHook().sendMessage("대기열에 추가되었습니다: " + firstTrack.getInfo().getTitle()
                    + "\n요청자: " + requesterText).queue();
        }

        if(onTrackLoadedCallback != null) {
            onTrackLoadedCallback.run();
        }
    }

    @Override
    public void noMatches() {
        System.out.println("[AudioLoader] noMatches 호출됨");
        if(!silent) {
            event.getHook().sendMessage("입력한 내용과 일치하는 항목이 없습니다!").queue();
        }
        if(onTrackLoadedCallback != null) {
            onTrackLoadedCallback.run();
        }

    }

    @Override
    public void loadFailed(@NotNull LoadFailed result) {
        System.out.println("[AudioLoader] loadFailed 호출됨: " + result.getException().getMessage());
        if(!silent) {
            event.getHook().sendMessage("트랙을 불러오지 못했습니다! 오류: " + result.getException().getMessage()).queue();
        }
        if(onTrackLoadedCallback != null) {
            onTrackLoadedCallback.run();
        }
    }
}
