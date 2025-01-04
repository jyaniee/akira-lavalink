package akira.commands;

import akira.listener.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Join {
    private final CommandHandler commandHandler;

    public Join(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if(member == null) {
            event.reply("사용자를 확인할 수 없습니다.").queue();
            return;
        }

        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(memberVoiceState != null && memberVoiceState.inAudioChannel()){
            var channel = memberVoiceState.getChannel();
            var guildMusicManager = commandHandler.getOrCreateMusicManager(channel.getGuild().getIdLong());

            if(event.getMessageChannel() instanceof TextChannel textChanel) {
                guildMusicManager.setTextChannel(textChanel);
            }
            
            // 연결
            event.getJDA().getDirectAudioController().connect(memberVoiceState.getChannel());

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("\uD83D\uDD17 음성 채널에 참가합니다!");
            embed.setDescription("봇이 음성 채널 `" + channel.getName() + "`에 참가했습니다.");
            embed.setColor(0x1DB954);
            // event.reply("채널에 참가합니다.").queue();
            event.replyEmbeds(embed.build()).queue();
        } else {
            event.reply("먼저 음성 채널에 들어가세요!").queue();
        }
    }
}
