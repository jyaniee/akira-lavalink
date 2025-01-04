package akira.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Leave {
    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(guild == null){
            return;
        }
        event.getJDA().getDirectAudioController().disconnect(guild);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83D\uDD0C 음성 채널에서 나갑니다.");
        embed.setDescription("봇이 음성 채널을 떠났습니다.");
        embed.setColor(0xE74C3C);
        // event.reply("채널에서 나갑니다.").queue();
        event.replyEmbeds(embed.build()).queue();
    }
}
