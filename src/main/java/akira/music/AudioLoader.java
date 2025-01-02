package akira.music;

import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AudioLoader {
    private final Link link;

    public AudioLoader(Link link) {
        this.link = link;
    }

    public void loadAndPlay(String trackUrl, MessageReceivedEvent event) {

    }
}
