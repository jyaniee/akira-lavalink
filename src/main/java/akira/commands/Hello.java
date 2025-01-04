package akira.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Hello {
    public void sayHello(SlashCommandInteractionEvent event) {
        String userTag = event.getUser().getAsMention();

       // String replyMessage = "안녕하세요! " + userTag + " 님! \uD83D\uDC4B";

       // event.reply(replyMessage).queue();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("👋 안녕하세요!");
        embed.setDescription(userTag + " 님, 만나서 반갑습니다! 오늘도 좋은 하루 되세요. 😊");
        embed.setColor(0x3498DB); // 파란색
        event.replyEmbeds(embed.build()).queue();
    }
}
