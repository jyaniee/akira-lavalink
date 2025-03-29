package akira.music;

import akira.MyUserData;
import dev.arbjerg.lavalink.client.player.PlaylistLoaded;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.client.player.TrackLoaded;
import dev.arbjerg.lavalink.protocol.v4.Message;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.LocalDateTime;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class TrackScheduler {
    private final GuildMusicManager guildMusicManager;
    public final Queue<Track> queue = new LinkedList<>();

    // 이미 재생한 트랙의 식별자 저장
    private final Set<String> playedTrackIds = new HashSet<>();

    public TrackScheduler(GuildMusicManager guildMusicManager) {
        this.guildMusicManager = guildMusicManager;
    }

    public void enqueue(Track track) {
        System.out.println("[TrackScheduler] Enqueue track: " + track.getInfo().getTitle());
        System.out.println("[TrackScheduler] Current queue size: " + this.queue.size());


        this.guildMusicManager.getPlayer().ifPresentOrElse(
                (player) -> {
                    if (player.getTrack() == null) {
                        System.out.println("[TrackScheduler] No track is currently playing. Starting track: " + track.getInfo().getTitle());
                        this.startTrack(track);
                    } else {
                        System.out.println("[TrackScheduler] Adding to queue: " + track.getInfo().getTitle());
                        this.queue.offer(track);
                    }
                },
                () -> {
                    System.out.println("[TrackScheduler] Player not found, starting track immediately: " + track.getInfo().getTitle());
                    this.startTrack(track);
                }
        );
        System.out.println("[TrackScheduler] Queue size after enqueue: " + this.queue.size());
    }

    public void enqueuePlaylist(List<Track> tracks) {
        this.queue.addAll(tracks);

        this.guildMusicManager.getPlayer().ifPresentOrElse(
                (player) -> {
                    if (player.getTrack() == null) {
                        this.startTrack(this.queue.poll());
                    }
                },
                () -> {
                    this.startTrack(this.queue.poll());
                }
        );
    }

    public void onTrackStart(Track track) {
        // Your homework: Send a message to the channel somehow, have fun!
        // ㅇㅋ 메시지 보내기 추가
        this.guildMusicManager.getTextChannel().ifPresent(textChannel -> {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("\uD83C\uDFB5 현재 재생 중: " + track.getInfo().getTitle(), track.getInfo().getUri());

            embed.setDescription("이 순간을 음악으로 채워보세요. ✨");
            // 곡 길이 (포맷팅: mm:ss)
            long durationMs = track.getInfo().getLength();
            String formattedDuration = String.format("%02d:%02d",
                    (durationMs / 1000) / 60, // 분
                    (durationMs / 1000) % 60  // 초
            );
            embed.addField("곡 길이", formattedDuration, true); // 인라인 필드
            embed.addField("요청자", "<@" + track.getUserData(MyUserData.class).requester() + ">", false);

            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            embed.setFooter("• " + currentTime, null);
            embed.setColor(0x1DB954); // Spotify 그린 컬러 (16진수 색상 코드)
            embed.setThumbnail("https://img.youtube.com/vi/" + track.getInfo().getIdentifier() + "/hqdefault.jpg"); // 썸네일 이미지
            // String message = "\uD83C\uDFB5 현재 재생 중: **" + track.getInfo().getTitle() + "**\n\uD83D\uDD17 [곡 링크](" + track.getInfo().getUri() + ")";
            // textChannel.sendMessage(message).queue();

            textChannel.sendMessageEmbeds(embed.build()).queue();
        });
        System.out.println("Track started: " + track.getInfo().getTitle());
    }

    public void onTrackEnd(Track lastTrack, Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason endReason) {
        if (endReason.getMayStartNext()) {
            System.out.println("[TrackScheduler] Track ended: " + lastTrack.getInfo().getTitle());

            // 방금 끝난 트랙 ID 추가
            playedTrackIds.add(lastTrack.getInfo().getIdentifier());

            final var nextTrack = this.queue.poll();

            if (nextTrack != null) {
                System.out.println("[TrackScheduler] Next track to play: " + nextTrack.getInfo().getTitle());
                this.startTrack(nextTrack);
            } else if ("youtube".equals(lastTrack.getInfo().getSourceName()) || "youtubemusic".equals(lastTrack.getInfo().getSourceName())) {
                // 대기열이 비었을 때 연관 동영상 리스트 가져옴
                String relatedVideosUrl = "https://www.youtube.com/watch?v=" + lastTrack.getInfo().getIdentifier()
                        + "&list=RD" + lastTrack.getInfo().getIdentifier();
                System.out.println("[TrackScheduler] 연관 동영상 검색: " + relatedVideosUrl);

                this.guildMusicManager.getLink().ifPresent(link ->
                        link.loadItem(relatedVideosUrl).subscribe(trackResult -> {
                            if (trackResult instanceof TrackLoaded trackLoaded) {
                                Track newTrack = trackLoaded.getTrack();
                                if(!playedTrackIds.contains(newTrack.getInfo().getIdentifier())) {
                                    startTrack(newTrack);
                                }else{
                                    System.out.println("[TrackScheduler] 로드된 단일 트랙이 중복되어 무시됨: " + newTrack.getInfo().getTitle());
                                }
                               // startTrack(((TrackLoaded) trackResult).getTrack());
                            } else if (trackResult instanceof PlaylistLoaded playlistLoaded) {
                                playlistLoaded.getTracks().stream()
                                        .filter(track -> !playedTrackIds.contains(track.getInfo().getIdentifier()))
                                        .findFirst()
                                        .ifPresentOrElse(
                                                this::startTrack,
                                                () -> System.out.println("[TrackScheduler] 중복되지 않은 트랙을 찾을 수 없습니다.")
                                        );
                                /* PlaylistLoaded playlist = (PlaylistLoaded) trackResult;
                                Track firstTrack = playlist.getTracks().stream()
                                        .filter(t -> !t.getInfo().getIdentifier().equals(lastTrack.getInfo().getIdentifier())) // 중복 제거
                                        .findFirst()
                                        .orElse(null);

                                if (firstTrack != null) {
                                    startTrack(firstTrack);
                                }
                                 */
                            }
                        })
                );
            }
        }
    }

    public void startTrack(Track track) {
        // 재생할 때마다 Set에 트랙 ID 저장
        playedTrackIds.add(track.getInfo().getIdentifier());
        this.guildMusicManager.getLink().ifPresent(
                (link) -> link.createOrUpdatePlayer()
                        .setTrack(track)
                        .setVolume(35)
                        .subscribe()
        );
    }
}