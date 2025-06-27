package akira.maple.commands;

import akira.maple.MapleScouterClient;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class CharacterInfo {

    private final MapleScouterClient client = new MapleScouterClient();

    public void execute(SlashCommandInteractionEvent event) {
        String nickname = event.getOption("닉네임").getAsString();

        event.deferReply().queue(); // 로딩 상태 표시

        MapleScouterClient client = new MapleScouterClient();
        JsonObject[] result = client.fetchCharacterInfo(nickname);

        if (result == null || result.length != 2) {
            event.getHook().sendMessage("❌ 정보를 가져오지 못했습니다.").queue();
            return;
        }

        JsonObject info = result[0];
        JsonObject calc = result[1];

        String name = info.get("character_name").getAsString();
        String world = info.get("world_name").getAsString();
        String characterClass = info.get("character_class").getAsString();
        int level = info.get("character_level").getAsInt();
        String expRate = info.get("character_exp_rate").getAsString();
        String image = info.get("character_image").getAsString();

        long combatPower = calc.get("combatPower").getAsLong();
        int stat = calc.get("boss300_stat").getAsInt();
        int hexaStat = calc.get("boss300_hexaStat").getAsInt();

        String formattedPower = formatCombatPower(combatPower);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("메이플 기본 정보")
                .setThumbnail(image)
                .addField("닉네임", name, true)
                .addField("서버", world, true)
                .addField("직업", characterClass, true)
                .addField("레벨", level + " (" + expRate + "%)", false)
                .addField("전투력", formattedPower, false)
                .addField("환산", String.format("%,d", stat), true)
                .addField("헥사환산", String.format("%,d", hexaStat), true)
                .setColor(Color.PINK);

        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }

    private String formatCombatPower(long power) {
        long eok = power / 100_000_000;
        long man = (power % 100_000_000) / 10_000;
        long rest = power % 10_000;

        if (eok > 0) {
            return String.format("%d억 %,d만 %,d", eok, man, rest);
        } else if (man > 0) {
            return String.format("%d만 %,d", man, rest);
        } else {
            return String.format("%,d", rest);
        }
    }
}
