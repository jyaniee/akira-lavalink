package akira.commands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Join {
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if(memberVoiceState != null && memberVoiceState.inAudioChannel()){
            event.getJDA().getDirectAudioController().connect(memberVoiceState.getChannel());
            event.reply("채널에 참가합니다.").queue();
        } else {
            event.reply("먼저 음성 채널에 들어가세요!").queue();
        }
    }
}
