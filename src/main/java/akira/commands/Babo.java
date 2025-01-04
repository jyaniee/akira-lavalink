package akira.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;

public class Babo {
    private final Map<String, Integer> usageCount = new HashMap<>(); // 사용 횟수 누적 ^^

    public void execute(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();
        String userTag = event.getUser().getAsMention();

        int count = usageCount.getOrDefault(userId, 0) + 1;
        usageCount.put(userId, count);

        String warning = count >= 2
                ? "\n⚠️ 사용 횟수가 " + count + "회입니다. 너무 많이 사용하면 무슨 일이 일어날지 모릅니다.❗"
                : "\n⚠️ 현재 사용 횟수: " + count + "회";

        // String fuckYou = userTag + " 응~ 넌 바보 멍청이 설사 똥개 해삼 말미쟐 \uD83D\uDE1C";
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("🤪 바보 감지!");
        embed.setDescription(userTag + " 응~ 넌 바보 멍청이 설사 똥개 해삼 말미쟐 ㅗㅗ \uD83D\uDCA9");
        embed.setColor(count >= 5 ? 0xE74C3C : 0xF1C40F); // 5회 이상이면 레드, 아니면 골드톤
        embed.setFooter(warning, null);

        // 임베드로 응답
        event.replyEmbeds(embed.build()).queue();
        // event.reply(fuckYou).queue();
    }
}
