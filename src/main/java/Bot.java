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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

@Slf4j
public class Bot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        JDABuilder jdaBuilder = JDABuilder.createDefault("Nzg2MTQ1NTI0ODA5NzI4MDAw.X9CJEg._DielBHtcMZnGsG4hqcpKV0KceA");

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
    private enum States{EMPTY,BUZZIG};
    private static States state = States.EMPTY;
    private static String prefix;
    private void handleMessage(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String[] content = message.getContentRaw().split(" ");
        String command = content[0];
        state = state.EMPTY;
        MessageChannel channel = event.getChannel();
        prefix ="!";

        if (command.startsWith(prefix)) {
            command = command.toLowerCase(Locale.ROOT).replace(prefix, "");

            switch (command) {
                case("help"):
                {
                    channel.sendMessage("some helpy stuff").queue();
                    break;
                }
                case ("hellothere"): {

                    channel.sendMessage("General Kenoby!").queue();
                    break;
                }
                case ("startbuzzer"): {
                    //select if with or without buzzer sound.
                    channel.sendMessage("stated Buzzering").queue();
                    state = States.BUZZIG;
                    break;
                }
                case ("buzz"): {
                    if (state == States.BUZZIG) {
                        state = States.EMPTY;

                        channel.sendMessage(event.getAuthor() + "was first to buzzer").queue();
                        //play buzzer sound.
                    }
                    break;
                }
                case ("cp"):
                case ("chanceprefix"): {
                    if(content.length<2) {
                        channel.sendMessage("not enough arguments").queue();
                        break;
                    }
                    else
                        {
                        prefix = content[1];
                        channel.sendMessage("Prefix is now: " + prefix).queue();
                        break;
                    }
                }
                case("pdu"):
                case ("plotdiagramugly"): {
                    //make a Diagram with colectet data: 1.Abstimmung per reactions 2.Annonyme Abstimmung (realisirung noch unklar)

                    int width = 250;
                    int height = 250;

                    // Constructs a BufferedImage of one of the predefined image types.
                    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                    // Create a graphics which can be used to draw into the buffered image
                    Graphics2D g2d = bufferedImage.createGraphics();

                    // fill all the image with white
                    g2d.setColor(Color.white);
                    g2d.fillRect(0, 0, width, height);

                    // create a circle with black
                    g2d.setColor(Color.black);
                    g2d.fillOval(0, 0, width, height);

                    // create a string with yellow
                    g2d.setColor(Color.yellow);
                    g2d.drawString("this is a diagram", 50, 120);

                    // Disposes of this graphics context and releases any system resources that it is using.
                    g2d.dispose();

                    // Save as PNG
                    File file = new File("src/main/misc/dataoutput.png");
                    try {
                        ImageIO.write(bufferedImage, "png", file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //send the Diagram
                    try {
                        channel.sendFile(file).queue();
                    }catch (Exception e) {
                        channel.sendMessage(""+e).queue();
                    }
                    break;
                }
                case("pd"):
                case("plotdiagram"):
                {
                    //make a Diagram with colectet data: 1.Abstimmung per reactions 2.Annonyme Abstimmung (realisirung noch unklar)
                    try {
                        Process p = Runtime.getRuntime().exec("python ../python/plotter.py");
                        //channel.sendMessage("drawn diagram").queue();
                    } catch (IOException e) {
                        channel.sendMessage("Error: "+e).queue();
                    }


                    //send the Diagram
                    try {
                        File file = new File("src/main/misc/dataoutput.png");
                        channel.sendFile(file).queue();
                    }catch (Exception e) {
                        channel.sendMessage(""+e).queue();
                    }
                    break;
                }
                default:
                    channel.sendMessage("Invalid command, type "+prefix+"help for help").queue();
            }
        }


    }
}