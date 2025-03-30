package akira.commands;

import akira.MyUserData;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class NowPlaying {
    private final LavalinkClient client;

    public NowPlaying(LavalinkClient client) {
        this.client = client;
    }

    private String formatTIme(long millis){
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60));

        if(hours > 0){
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public void execute(SlashCommandInteractionEvent event){
        Guild guild = event.getGuild();
        final var link = this.client.getOrCreateLink(guild.getIdLong());
        final var player = link.getCachedPlayer();

        if(player == null){
            // event.reply("채널에 연결되어있지 않거나 플레이어가 존재하지 않습니다.").queue();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("⚠️ 플레이어가 존재하지 않습니다.");
            embed.setDescription("봇이 음성 채널에 연결되어 있지 않거나 현재 활성화된 플레이어가 없습니다.");
            embed.setColor(0xE74C3C); // 레드톤
            event.replyEmbeds(embed.build()).queue();
            return;
        }
        final var track = player.getTrack();

        if(track == null){
           // event.reply("재생 중인 음악이 없습니다!").queue();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("⚠️ 재생 중인 음악이 없습니다!");
            embed.setDescription("현재 재생 중인 음악이 없습니다. 플레이어에 곡을 추가해보세요.");
            embed.setColor(0xE74C3C); // 레드톤
            event.replyEmbeds(embed.build()).queue();
            return;
        }

        final var trackInfo = track.getInfo();;

        String currentTime = formatTIme(player.getPosition());
        String trackLength = formatTIme(trackInfo.getLength());


       /* event.reply(
                "현재 재생 중: %s\n재생 시간: %s/%s\n요청자: <@%s>".formatted(
                        trackInfo.getTitle(),
                        currentTime,
                        trackLength,
                        track.getUserData(MyUserData.class).requester()
                )
        ).queue();

        */

        long requesterId = track.getUserData(MyUserData.class).requester();
        long botId = event.getJDA().getSelfUser().getIdLong();
        String requesterText = (requesterId == 0L) ? "<@" + botId + ">" : "<@" + requesterId + ">";
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("🎵 현재 재생 중: " + trackInfo.getTitle(), trackInfo.getUri());
        embed.setDescription("**재생 시간:** `%s / %s`".formatted(currentTime, trackLength));
        embed.setColor(0x1DB954);
        embed.setThumbnail("https://img.youtube.com/vi/" + trackInfo.getIdentifier() + "/hqdefault.jpg"); // 썸네일 이미지
        // embed.addField("요청자", "<@" + track.getUserData(MyUserData.class).requester() + ">", false);
        embed.addField("요청자", requesterText, false);
        event.replyEmbeds(embed.build()).queue();
    }
}
