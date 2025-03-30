package akira.commands;

import akira.listener.CommandHandler;
import akira.music.GuildMusicManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Leave {
    private final CommandHandler commandHandler;

    public Leave(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(guild == null){
            return;
        }
        event.getJDA().getDirectAudioController().disconnect(guild);

        // 대기열 초기화
        GuildMusicManager musicManager = commandHandler.getOrCreateMusicManager(guild.getIdLong());
        musicManager.stop(); // queue.clear(), stop track
        musicManager.scheduler.reset();
        musicManager.setTextChannel(null);


        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83D\uDD0C 음성 채널에서 나갑니다.");
        embed.setDescription("봇이 음성 채널을 떠났습니다. 대기열이 초기화됩니다.");
        embed.setColor(0xE74C3C);
        // event.reply("채널에서 나갑니다.").queue();
        event.replyEmbeds(embed.build()).queue();
    }
}
