package akira.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Babo {
    public void execute(SlashCommandInteractionEvent event) {
        String userTag = event.getUser().getAsMention();

        String fuckYou = userTag + " 응~ 넌 바보 멍청이 설사 똥개 해삼 말미쟐 \uD83D\uDE1C";

        event.reply(fuckYou).queue();
    }
}
