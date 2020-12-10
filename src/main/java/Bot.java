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

import javax.security.auth.login.LoginException;
import java.io.File;

@Slf4j
public class Bot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        JDABuilder jdaBuilder = JDABuilder.createDefault("Nzg2MTQ1NTI0ODA5NzI4MDAw.X9CJEg.wIPiTL_HImLtsY3QKByTeuiA_Ps");

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

        }
    }

    private void handleMessage(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        String state = "";
        MessageChannel channel = event.getChannel();

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