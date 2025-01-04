package akira.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Hello {
    public void sayHello(SlashCommandInteractionEvent event) {
        String userTag = event.getUser().getAsMention();

        String replyMessage = "안녕하세요! " + userTag + " 님! \uD83D\uDC4B";

        event.reply(replyMessage).queue();
    }
}
