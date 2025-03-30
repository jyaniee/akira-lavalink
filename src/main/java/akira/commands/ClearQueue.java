package akira.commands;

import akira.listener.CommandHandler;
import akira.music.GuildMusicManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ClearQueue {
    private final CommandHandler commandHandler;

    public ClearQueue(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(guild == null) return;

        GuildMusicManager musicManager = commandHandler.getOrCreateMusicManager(guild.getIdLong());
        musicManager.stop();
        musicManager.scheduler.reset();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83D\uDDD1\uFE0F 대기열 초기화 완료");
        embed.setDescription("모든 대기열이 제거되었습니다.");
        embed.setColor(0xF1C40F);

        event.replyEmbeds(embed.build()).queue();
    }
}
