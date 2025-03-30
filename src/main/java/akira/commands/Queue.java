package akira.commands;

import akira.MyUserData;
import akira.listener.CommandHandler;
import akira.music.GuildMusicManager;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import akira.music.TrackScheduler;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Queue {
    private final LavalinkClient client;
    private final CommandHandler commandHandler;
    public static final Map<String, Integer> userPageMap = new HashMap<>();

    public Queue(LavalinkClient client, CommandHandler commandHandler) {
        this.client = client;
        this.commandHandler = commandHandler;
    }

    public void execute(SlashCommandInteractionEvent event) {
        var guild = event.getGuild();
        if (guild == null) {
            return;
        }

        final long guildId = guild.getIdLong();
        GuildMusicManager musicManager = commandHandler.getOrCreateMusicManager(guildId);

        TrackScheduler scheduler = musicManager.scheduler;

        if (scheduler.queue.isEmpty()) {
            // event.reply("현재 대기열이 비어 있습니다!").queue();
            EmbedBuilder emptyEmbed = new EmbedBuilder();
            emptyEmbed.setTitle("🎵 대기열이 비어 있습니다!");
            emptyEmbed.setDescription("현재 대기열에 추가된 곡이 없습니다.");
            emptyEmbed.setColor(0xE74C3C); // 레드톤
            event.replyEmbeds(emptyEmbed.build()).queue();
            return;
        }

        String userKey = event.getUser().getId() + ":" + guildId;
        userPageMap.put(userKey, 1);

        var embed = buildQueueEmbed(scheduler.queue, 1);
        event.replyEmbeds(embed.build())
                .addActionRow(
                        Button.primary("queue_prev", "⬅ 이전"),
                        Button.primary("queue_next", "다음 ➡")
                ).queue();
    }
    /*
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

     */

    public EmbedBuilder buildQueueEmbed(java.util.Queue<Track> queue, int page) {
        int itemsPerPage = 10;
        int totalTracks = queue.size();
        int totalPages = (int) Math.ceil((double) totalTracks / itemsPerPage);

        page = Math.max(1, Math.min(page, totalPages));
        List<Track> tracks = queue.stream().toList();
        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, totalTracks);

        List<String> trackList = new ArrayList<>();
        for (int i = start; i < end; i++) {
            Track track = tracks.get(i);
            MyUserData userData = track.getUserData(MyUserData.class);
            long requesterId = userData.getRequesterId();
            String sourceType = userData.getSourceType();

            String requesterText;
            switch (sourceType) {
                case "jpop" -> requesterText = "`JPOP 리스트` (by <@" + requesterId + ">)";
                default     -> requesterText = "<@" + requesterId + ">";
            }

            trackList.add(String.format("%d. 🎵 %s — %s",
                    i + 1,
                    track.getInfo().getTitle(),
                    requesterText));
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("🎶 현재 대기열");
        embed.setDescription(String.join("\n", trackList));
        embed.setFooter("페이지 %d/%d | 총 %d곡".formatted(page, totalPages, totalTracks), null);
        embed.setColor(0x1DB954);
        return embed;
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
