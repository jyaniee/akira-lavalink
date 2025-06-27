package akira.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Optional;

public class GuildMusicManager {
    public final TrackScheduler scheduler = new TrackScheduler(this);
    private final long guildId;
    private final LavalinkClient lavalink;
    private TextChannel textChannel;

    private int currentVolume = 35;

    public int getCurrentVolume() {
        return currentVolume;
    }

    public void setCurrentVolume(int currentVolume) {
        this.currentVolume = currentVolume;
    }

    public GuildMusicManager(long guildId, LavalinkClient lavalink) {
        this.lavalink = lavalink;
        this.guildId = guildId;
    }

    public void setTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    public Optional<TextChannel> getTextChannel() {
        return Optional.ofNullable(this.textChannel);
    }

    public void stop() {
        this.scheduler.queue.clear();

        this.getPlayer().ifPresent(
                (player) -> player.setPaused(false)
                        .setTrack(null)
                        .subscribe()
        );
    }

    public Optional<Link> getLink() {
        return Optional.ofNullable(
                this.lavalink.getLinkIfCached(this.guildId)
        );
    }

    public Optional<LavalinkPlayer> getPlayer() {
        return this.getLink().map(Link::getCachedPlayer);
    }
}