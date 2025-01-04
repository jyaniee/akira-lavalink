package akira.commands;

import akira.listener.CommandHandler;
import akira.music.GuildMusicManager;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Volume {
    private final LavalinkClient client;
    private final CommandHandler commandHandler;

    public Volume(LavalinkClient client, CommandHandler commandHandler) {
        this.client = client;
        this.commandHandler = commandHandler;
    }

    public void execute(SlashCommandInteractionEvent event) {
        var guild = event.getGuild();
        if(guild == null) return;

        var volumeOption = event.getOption("볼륨");
        if(volumeOption == null){
            event.reply("볼륨 값을 입력하세요! (예: 0 ~ 100)").queue();
            return;
        }

        int volume = volumeOption.getAsInt();
        if(volume < 0 || volume > 100){
            event.reply("볼륨 값은 0에서 100 사이여야 합니다.").queue();
            return;
        }

        GuildMusicManager musicManager = commandHandler.getOrCreateMusicManager(guild.getIdLong());
        musicManager.getPlayer().ifPresentOrElse(player -> {
            player.setVolume(volume).subscribe();
            event.reply("볼륨이 " + volume + "으로 설정되었습니다!").queue();
            System.out.println("[Volume] Volume has been set to " + volume);
        }, () -> {
            event.reply("현재 재생 중인 곡이 없습니다.").queue();
        });
    }
}
