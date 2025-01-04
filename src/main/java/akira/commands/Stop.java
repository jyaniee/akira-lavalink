package akira.commands;

import akira.listener.CommandHandler;
import akira.music.GuildMusicManager;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;

public class Stop {
    public final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();
    private final LavalinkClient client;
    private final CommandHandler commandHandler;
    public Stop(LavalinkClient client, CommandHandler commandHandler) {
        this.client = client;
        this.commandHandler = commandHandler;
    }

    public void execute(SlashCommandInteractionEvent event) {
       // event.reply("현재 트랙을 멈추고 재생 목록을 초기화합니다.").queue();
        commandHandler.getOrCreateMusicManager(event.getGuild().getIdLong()).stop();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("⏹️ 음악 재생 중단");
        embed.setDescription("현재 트랙을 멈추고 재생 목록을 초기화했습니다.");
        embed.setColor(0xE74C3C);
        embed.setFooter("요청자: " + event.getUser().getName(), event.getUser().getAvatarUrl());
        event.replyEmbeds(embed.build()).queue();
    }
/*
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

 */
}


