package akira.commands;

import akira.listener.CommandHandler;
import akira.music.AudioLoader;
import akira.music.GuildMusicManager;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Play {
    private final LavalinkClient client;
    public final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    private final CommandHandler commandHandler;

    public Play(LavalinkClient client, CommandHandler commandHandler) {
        this.client = client;
        this.commandHandler = commandHandler;
    }

    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return;

        String identifier = event.getOption("쿼리").getAsString();
        String source = event.getOption("플랫폼").getAsString();

        if(guild.getSelfMember().getVoiceState().inAudioChannel()){
            event.deferReply(false).queue();
        } else {
            new Join().execute(event);
        }

        final long guildId = event.getGuild().getIdLong();
        final Link link = this.client.getOrCreateLink(guildId);
        final var mngr = commandHandler.getOrCreateMusicManager(guildId);

        if(isValidURI(identifier)){
            // URI일 경우
            link.loadItem(identifier).subscribe(new AudioLoader(event, mngr));
        } else {
            // 제목일 경우
            String searchQuery = source + ":" + identifier; // "ytsearch:노래제목"
            link.loadItem(searchQuery).subscribe(new AudioLoader(event, mngr));
        }

    }

    private boolean isValidURI(String identifier) {
        try {
            new URI(identifier);
            return true;
        } catch (URISyntaxException e){
            return false;
        }
    }
/*
    public GuildMusicManager getOrCreateMusicManager(long guildId) {
        synchronized(this) {
            var mng = this.musicManagers.get(guildId);

            if (mng == null) {
                mng = new GuildMusicManager(guildId, this.client);
                this.musicManagers.put(guildId, mng);
            }

            return mng;
        }
    }
 */
}
