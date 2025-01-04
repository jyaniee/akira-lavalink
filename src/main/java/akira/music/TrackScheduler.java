package akira.music;

import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.protocol.v4.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TrackScheduler {
    private final GuildMusicManager guildMusicManager;
    public final Queue<Track> queue = new LinkedList<>();

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
        System.out.println("Track started: " + track.getInfo().getTitle());
    }

    public void onTrackEnd(Track lastTrack, Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason endReason) {
        if (endReason.getMayStartNext()) {
            System.out.println("[TrackScheduler] Track ended: " + lastTrack.getInfo().getTitle());
            final var nextTrack = this.queue.poll();

            if (nextTrack != null) {
                System.out.println("[TrackScheduler] Next track to play: " + nextTrack.getInfo().getTitle());
                this.startTrack(nextTrack);
            }
        }
    }

    public void startTrack(Track track) {
        this.guildMusicManager.getLink().ifPresent(
                (link) -> link.createOrUpdatePlayer()
                        .setTrack(track)
                        .setVolume(35)
                        .subscribe()
        );
    }
}