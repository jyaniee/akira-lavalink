package akira.commands;

import akira.listener.CommandHandler;
import akira.music.GuildMusicManager;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.EmbedBuilder;
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
            int currentVolume = commandHandler.getOrCreateMusicManager(guild.getIdLong()).getCurrentVolume();
            EmbedBuilder infoEmbed = new EmbedBuilder();
            infoEmbed.setTitle("🔈 현재 볼륨");
            infoEmbed.setDescription("현재 볼륨은 **" + currentVolume + "**입니다.");
            infoEmbed.setColor(0x3498DB);
            event.replyEmbeds(infoEmbed.build()).queue();
            return;
            /*
            // event.reply("볼륨 값을 입력하세요! (예: 0 ~ 100)").queue();
            EmbedBuilder errorEmbed = new EmbedBuilder();
            errorEmbed.setTitle("⚠️ 볼륨 값 누락");
            errorEmbed.setDescription("볼륨 값을 입력하세요! (예: 0 ~ 100)");
            errorEmbed.setColor(0xE74C3C);
            event.replyEmbeds(errorEmbed.build()).queue();

            return;*/
        }

        int volume = volumeOption.getAsInt();
        if(volume < 0 || volume > 100){
            // event.reply("볼륨 값은 0에서 100 사이여야 합니다.").queue();
            EmbedBuilder errorEmbed = new EmbedBuilder();
            errorEmbed.setTitle("⚠️ 잘못된 볼륨 값");
            errorEmbed.setDescription("볼륨 값은 0에서 100 사이여야 합니다.");
            errorEmbed.setColor(0xE74C3C);
            event.replyEmbeds(errorEmbed.build()).queue();
            return;
        }

        GuildMusicManager musicManager = commandHandler.getOrCreateMusicManager(guild.getIdLong());
        musicManager.setCurrentVolume(volume);
        musicManager.getPlayer().ifPresentOrElse(player -> {
            player.setVolume(volume).subscribe();
            EmbedBuilder successEmbed = new EmbedBuilder();
            successEmbed.setTitle("🔊 볼륨 조정 완료");
            successEmbed.setDescription("볼륨이 **" + volume + "**으로 설정되었습니다!");
            successEmbed.setColor(0x1DB954);
            successEmbed.setFooter("요청자: " + event.getUser().getName(), event.getUser().getAvatarUrl());
            // event.reply("볼륨이 " + volume + "으로 설정되었습니다!").queue();
            event.replyEmbeds(successEmbed.build()).queue();
            System.out.println("[Volume] Volume has been set to " + volume);
        }, () -> {
            // event.reply("현재 재생 중인 곡이 없습니다.").queue();
            EmbedBuilder errorEmbed = new EmbedBuilder();
            errorEmbed.setTitle("⚠️ 곡 없음");
            errorEmbed.setDescription("현재 재생 중인 곡이 없습니다.");
            errorEmbed.setColor(0xE74C3C);
            event.replyEmbeds(errorEmbed.build()).queue();
        });
    }
}
