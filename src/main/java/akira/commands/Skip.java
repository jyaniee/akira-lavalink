package akira.commands;

import akira.listener.CommandHandler;
import akira.music.GuildMusicManager;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Skip {
    private final LavalinkClient client;
    private final CommandHandler commandHandler;

    public Skip(LavalinkClient client, CommandHandler commandHandler) {
        this.client = client;
        this.commandHandler = commandHandler;
    }

    public void execute(SlashCommandInteractionEvent event) {
        var guild = event.getGuild();
        if(guild == null) return;

        GuildMusicManager musicManager = commandHandler.getOrCreateMusicManager(guild.getIdLong());

        musicManager.getPlayer().ifPresentOrElse(player -> {
            var scheduler = musicManager.scheduler;
            if(scheduler.queue.isEmpty()){
                player.setTrack(null).subscribe();
                // event.reply("현재 재생 중인 곡을 스킵했습니다. 대기열이 비어 있습니다.").queue();
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("⏭️ 곡 스킵");
                embed.setDescription("현재 재생 중인 곡을 스킵했습니다.\n\n**대기열이 비어 있습니다.**");
                embed.setColor(0xE74C3C);
                embed.setFooter("요청자: " + event.getUser().getName(), event.getUser().getAvatarUrl());
                event.replyEmbeds(embed.build()).queue();

            } else {
                var nextTrack = scheduler.queue.poll();
                scheduler.startTrack(nextTrack);
                // event.reply("현재 곡을 스킵하고 다음 곡을 재생합니다: "+ nextTrack.getInfo().getTitle()).queue();
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("⏭️ 곡 스킵");
                embed.setDescription("현재 곡을 스킵하고 다음 곡을 재생합니다:\n**" + nextTrack.getInfo().getTitle() + "**");
                embed.setColor(0x1DB954);
                embed.setThumbnail("https://img.youtube.com/vi/" + nextTrack.getInfo().getIdentifier() + "/hqdefault.jpg"); // 썸네일 이미지
                embed.setFooter("요청자: " + event.getUser().getName(), event.getUser().getAvatarUrl());

                event.replyEmbeds(embed.build()).queue();
            }
        }, () -> {
           // event.reply("현재 재생 중인 곡이 없습니다.").queue();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("⚠️ 재생 중인 곡 없음");
            embed.setDescription("현재 재생 중인 곡이 없습니다.");
            embed.setColor(0xE74C3C);
            embed.setFooter("요청자: " + event.getUser().getName(), event.getUser().getAvatarUrl());

            event.replyEmbeds(embed.build()).queue();
        });
    }
}
