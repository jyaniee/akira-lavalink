package akira.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Leave {
    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if(guild == null){
            return;
        }
        event.getJDA().getDirectAudioController().disconnect(guild);
        event.reply("채널에서 나갑니다.").queue();
    }
}
