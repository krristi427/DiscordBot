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
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        try{
            log.info("Received message with text: {}", event.getMessage().getContentRaw());
            handleMessage(event);
        }catch (Exception e){
            log.warn("Could not process message", e);
            event.getMessage().getChannel().sendMessage("Unexpected error occurred!").queue();

        }
    }
    private enum States{EMPTY,BUZZIG};
    private States state = States.EMPTY;
    private void handleMessage(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String[] content = message.getContentRaw().split(" ");
        String command = content[0];
        state = state.EMPTY;
        MessageChannel channel = event.getChannel();
        String prefix ="!";

        switch(content)
        {
            case("!helloThere"):
            {

                channel.sendMessage("General Kenoby!").queue();
                break;
            }
            case("!startBuzzer"):
            {
                //select if with or without buzzer sound.
                channel.sendMessage("stated Buzzering").queue();
                state="buzzering";
                break;
            }
            case("!Buzz"):
            {
                if(state=="buzzering")
                {
                    state="";

                    channel.sendMessage(event.getAuthor()+"was first to buzzer").queue();
                    //play buzzer sound.
                }
                break;
            }
            case("plotDiagram"):
            {
                //make a Diagram with colectet data: 1.Abstimmung per reactions 2.Annonyme Abstimmung (realisirung noch unklar)

                //send the Diagram
                File file = new File("C:/test");
                channel.sendFile(file).queue();
            }
        }

    }
}