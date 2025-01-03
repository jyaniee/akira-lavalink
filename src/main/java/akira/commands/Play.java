package akira.commands;

import akira.music.AudioLoader;
import akira.music.GuildMusicManager;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;

public class Play {
    private final LavalinkClient client;
    public final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    public Play(LavalinkClient client) {
        this.client = client;
    }

    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return;
        if(guild.getSelfMember().getVoiceState().inAudioChannel()){
            event.deferReply(false).queue();
        } else {
            new Join().execute(event);
        }

        final String identifier = event.getOption("identifier").getAsString();
        final long guildId = event.getGuild().getIdLong();
        final Link link = this.client.getOrCreateLink(guildId);
        final var mngr = this.getOrCreateMusicManager(guildId);

        link.loadItem(identifier).subscribe(new AudioLoader(event, mngr));
    }

    private GuildMusicManager getOrCreateMusicManager(long guildId) {
        synchronized(this) {
            var mng = this.musicManagers.get(guildId);

            if (mng == null) {
                mng = new GuildMusicManager(guildId, this.client);
                this.musicManagers.put(guildId, mng);
            }

            return mng;
        }
    }
}
