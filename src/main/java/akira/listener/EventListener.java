package akira.listener;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter{
    private final LavalinkClient client;

    public EventListener(LavalinkClient client) {
        this.client = client;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();

        if(message.equalsIgnoreCase("!안녕")){
            event.getChannel().sendMessage("안녕하세요! \uD83D\uDE0A").queue();
        }

        if(message.equalsIgnoreCase("!재한바보")){
            String response = event.getAuthor().getAsMention() + " 너가 더 바보";
            System.out.println("Response: " + response);
            event.getChannel().sendMessage(response).queue();
        }

        if(message.startsWith("!play")){
            String trackUrl = message.split(" ", 2)[1];
            event.getChannel().sendMessage("재생합니다: " + trackUrl).queue();
            // 기능 추가 해야함(lavalink 문서 공부..)
        }
    }


}
