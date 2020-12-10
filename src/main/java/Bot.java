import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.GreetingService;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

@Slf4j
public class Bot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        JDABuilder jdaBuilder = JDABuilder.createDefault("");

        JDA build = jdaBuilder.build();
        build.addEventListener(new Bot());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try{
            log.info("Received message with text: {}", event.getMessage().getContentRaw());
            handleMessage(event);
        }catch (Exception e){
            log.warn("Could not process message", e);
            event.getMessage().getChannel().sendMessage("Unexpected error occurred!").queue();
            e.printStackTrace();
        }
    }

    private void handleMessage(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String contentRaw = message.getContentRaw();

        if (contentRaw.contains("!helloThere")) {
            GreetingService.getInstance().greetRequired(event.getChannel());

        } else if (contentRaw.contains("!createGreeting")) {
            GreetingService.getInstance().createGreeting(event.getMessage());
        }

    }
}