package akira.commands;

import akira.MyUserData;
import akira.listener.CommandHandler;
import akira.music.GuildMusicManager;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import akira.music.TrackScheduler;

import java.util.List;
import java.util.stream.Collectors;

public class Queue {
    private final LavalinkClient client;
    private final CommandHandler commandHandler;

    public Queue(LavalinkClient client, CommandHandler commandHandler) {
        this.client = client;
        this.commandHandler = commandHandler;
    }

    public void execute(SlashCommandInteractionEvent event) {
        var guild = event.getGuild();
        if(guild == null){
            return;
        }

        final long guildId = guild.getIdLong();
        GuildMusicManager musicManager = commandHandler.getOrCreateMusicManager(guildId);

        TrackScheduler scheduler = musicManager.scheduler;

        if(scheduler.queue.isEmpty()){
            // event.reply("현재 대기열이 비어 있습니다!").queue();
            EmbedBuilder emptyEmbed = new EmbedBuilder();
            emptyEmbed.setTitle("🎵 대기열이 비어 있습니다!");
            emptyEmbed.setDescription("현재 대기열에 추가된 곡이 없습니다.");
            emptyEmbed.setColor(0xE74C3C); // 레드톤
            event.replyEmbeds(emptyEmbed.build()).queue();
            return;
        }

        // 대기열 트랙 정보 가져옴
        List<String> trackList = scheduler.queue.stream()
                .limit(10)
                .map(track -> String.format("%s (요청자: <@%d>)",
                        track.getInfo().getTitle(),
                        track.getUserData(MyUserData.class).requester()))
                .collect(Collectors.toList());


        String queueMessage = String.join("\n", trackList);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("🎶 현재 대기열");
        embed.setDescription(queueMessage);
        embed.setColor(0x1DB954); // 그린톤
        embed.setFooter("총 " + scheduler.queue.size() + "개의 곡이 대기 중입니다.", null);

        event.replyEmbeds(embed.build()).queue();
        System.out.println("[Queue Command] Current queue:\n" + queueMessage);

        // event.reply("현재 대기열:\n" + queueMessage).queue();
    }
/*
    private GuildMusicManager getOrCreateMusicManager(long guildId) {
        return client.getLinks().stream()
                .filter(link -> link.getGuildId() == guildId)
                .findFirst()
                .map(link -> new GuildMusicManager(guildId, client))
                .orElseThrow(() -> new IllegalStateException("음악 매니저를 찾을 수 없습니다!"));
    }

 */
}
