package akira.commands;

import akira.listener.CommandHandler;
import akira.music.AudioLoader;
import akira.music.GuildMusicManager;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class DeveloperJpopList {
    private final LavalinkClient client;

    private final List<String> playlist = List.of(
            "ytsearch:晩餐歌",
            "ytsearch:地獄恋文",
            "ytsearch:はいよろこんで",
            "ytsearch:Mrs. GREEN APPLE - ライラック",
            "ytsearch:Rokudenashi - 知らないままで",
            "ytsearch:tuki. - ひゅるりらっぱっぱ",
            "ytsearch:Yuuri - ドライフラワー",
            "ytsearch:Official HIGE DANDISM - Pretender",
            "ytsearch:Yorushika - だから僕は音楽を辞めた",
            "ytsearch:Mrs. GREEN APPLE - 青と夏",
            "ytsearch:YOASOBI - 群青",
            "ytsearch:tuki. - 一輪花",
            "ytsearch:Yuuri - カーテンコール",
            "ytsearch:Tatsuya Kitani - 青のすみか",
            "ytsearch:Yorushika - 晴る",
            "ytsearch:Mrs. GREEN APPLE - インフェルノ",
            "ytsearch:YOASOBI - 怪物",
            "ytsearch:tuki. - 愛の賞味期限",
            "ytsearch:Yuuri - ベテルギウス",
            "ytsearch:SPYAIR - オレンジ",
            "ytsearch:Yorushika - 言って。",
            "ytsearch:Mrs. GREEN APPLE - ケセラセラ",
            "ytsearch:Utada Hikaru - First Love",
            "ytsearch:tuki. - サクラキミワタシ",
            "ytsearch:YOASOBI - アイドル",
            "ytsearch:Vaundy - 踊り子",
            "ytsearch:RADWIMPS - KANATA HALUKA",
            "ytsearch:Mrs. GREEN APPLE - ダンスホール",
            "ytsearch:友成空 - 鬼ノ宴",
            "ytsearch:tuki. - 星街の駅で",
            "ytsearch:Yorushika - ヒッチコック",
            "ytsearch:Kenshi Yonezu - さよーならまたいつか！",
            "ytsearch:RADWIMPS - Nandemonaiya - movie ver.",
            "ytsearch:natori - Overdose",
            "ytsearch:Rokudenashi - ただ声一つ",
            "ytsearch:tuki. - 純恋愛のインゴット",
            "ytsearch:Official HIGE DANDISM - 115万キロのフィルム",
            "ytsearch:YOASOBI - 夜に駆ける",
            "ytsearch:Vaundy - タイムパラドックス",
            "ytsearch:Ai Higuchi - Akuma no Ko",
            "ytsearch:Kenshi Yonezu - アイネクライネ",
            "ytsearch:Eve - Kaikai Kitan",
            "ytsearch:Aimyon - 君はロックを聴かない",
            "ytsearch:RADWIMPS, Toko Miura - Grand Escape",
            "ytsearch:Vaundy - Tokyo Flash",
            "ytsearch:tuki. - アイモライモ",
            "ytsearch:Kenshi Yonezu - 感電",
            "ytsearch:yama - Haru wo Tsugeru",
            "ytsearch:KANA-BOON - シルエット",
// ---------------------------------------------------------
            "ytsearch:Vacation - DURDN",
            "ytsearch:odoriko - Vaundy",
            "ytsearch:Supernatural - NewJeans",
            "ytsearch:Mayonaka no Door - Miki Matsubara",
            "ytsearch:Hai Yorokonde - Kocchi no Kento",
            "ytsearch:Bad example - たかやん",
            "ytsearch:カタオモイ - Aimer",
            "ytsearch:summertime - cinnamons",
            "ytsearch:Peace Sign - Kenshi Yonezu",
            "ytsearch:Shinunoga E-Wa - Fujii Kaze",
            "ytsearch:ビビデバ - Hoshimachi Suisei",
            "ytsearch:killer tune kills me - KIRINJI",
            "ytsearch:Kyouran Hey Kids!! - THE ORAL CIGARETTES",
            "ytsearch:De(vil) feat. yama - haruno",
            "ytsearch:RED OUT - Kenshi Yonezu",
            "ytsearch:色彩 - yama",
            "ytsearch:Remember Summer Days - Anri",
            "ytsearch:Zenzenzense - RADWIMPS",
            "ytsearch:Plastic Love - Mariya Takeuchi",
            "ytsearch:雪のすみか - Tatsuya Kitani",
            "ytsearch:ファタール - GEMN",
            "ytsearch:again - YUI",
            "ytsearch:想いきり - indigo la End",
            "ytsearch:One Last Kiss - Hikaru Utada",
            "ytsearch:Koi - Gen Hoshino",
            "ytsearch:SPECIALZ - King Gnu",
            "ytsearch:おもかげ - milet",
            "ytsearch:二十歳の恋 - Lamp",
            "ytsearch:残酷な天使のテーゼ - Yoko Takahashi",
            "ytsearch:Yesterday - Official HIGE DANDISM",
            "ytsearch:Marigold - Aimyon",
            "ytsearch:点描の唄 - Mrs. GREEN APPLE",
            "ytsearch:I Love... - Official HIGE DANDISM",
            "ytsearch:ないものねだり - KANA-BOON",
            "ytsearch:Ref:rain - Aimer",
            "ytsearch:恋愛サーキュレーション - 花澤香菜",
            "ytsearch:KICK BACK - Kenshi Yonezu",
            "ytsearch:Guren no Yumiya - Linked Horizon",
            "ytsearch:unravel - TK from Ling",
            "ytsearch:春を告げる - yama",
            "ytsearch:なんでもないや - RADWIMPS",
            "ytsearch:打上花火 - Daoko, Kenshi Yonezu"
    );

    private final CommandHandler commandHandler;

    public DeveloperJpopList(LavalinkClient client, CommandHandler commandHandler) {
        this.client = client;
        this.commandHandler = commandHandler;
    }

    public void execute(SlashCommandInteractionEvent event) {
        var guild = event.getGuild();
        if (guild == null) return;

        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = commandHandler.getOrCreateMusicManager(guildId);
        Link link = client.getOrCreateLink(guildId);
        /*
        event.deferReply().queue();

        for(String searchQuery : playlist) {
            link.loadItem(searchQuery).subscribe(new AudioLoader(event, musicManager));
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83C\uDF38 개발자의 JPOP 플레이리스트")
                .setDescription("대기열에 총 " + playlist.size() + "곡을 추가했어요!")
                .setThumbnail("https://mosaic.scdn.co/300/ab67616d00001e024fa36b14a276fe560940baa0ab67616d00001e0264c8b41faf576a0bab551fb9ab67616d00001e027e1eeb0d7cc374a168369c80ab67616d00001e028679d61504ed4718bf5f94ae")
                .setColor(0xFFC0CB)
                .setFooter("Made by " + event.getUser().getName(), event.getUser().getAvatarUrl());

        event.getHook().sendMessageEmbeds(embed.build()).queue();
         */
        event.deferReply().queue(hook -> {
            hook.sendMessage("🎶 JPOP 플레이리스트를 대기열에 추가하고 있어요... 잠시만 기다려 주세요!").queue(loadingMsg -> {
                final int total = playlist.size();
                final int[] completed = {0};
                final long botId = event.getJDA().getSelfUser().getIdLong();

                for (String searchQuery : playlist){
                    link.loadItem(searchQuery).subscribe(new AudioLoader(
                            event,
                            musicManager,
                            true, () -> {
                                completed[0]++;
                                if(completed[0] == total){
                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setTitle("🌸 개발자의 JPOP 플레이리스트")
                                            .setDescription("총 " + total + "곡이 모두 성공적으로 대기열에 추가되었습니다!")
                                            .setThumbnail("https://mosaic.scdn.co/300/ab67616d00001e024fa36b14a276fe560940baa0ab67616d00001e0264c8b41faf576a0bab551fb9ab67616d00001e027e1eeb0d7cc374a168369c80ab67616d00001e028679d61504ed4718bf5f94ae")
                                            .setColor(0xFFC0CB)
                                            .setFooter("Made by " + event.getUser().getName(), event.getUser().getAvatarUrl());

                                    loadingMsg.editMessageEmbeds(embed.build()).queue();
                        }
                    }, botId, true));
                }
            });
        });
    }
}