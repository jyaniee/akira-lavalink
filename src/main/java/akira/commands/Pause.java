package akira.commands;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Pause {
    private final LavalinkClient client;

    public Pause(LavalinkClient client) {
        this.client = client;
    }

    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        this.client.getOrCreateLink(guild.getIdLong())
                .getPlayer()
                .flatMap((player) -> player.setPaused(!player.getPaused()))
                .subscribe((player) -> {
                    event.reply("플레이어가 " + (player.getPaused() ? "일시 정지되었습니다." : "재생되었습니다.") + "!").queue();
                });
    }
}
