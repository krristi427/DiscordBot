import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import services.CommandsService;
import services.GreetingService;
import services.Sound;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Locale;

@Slf4j
public class Bot extends ListenerAdapter {

    //TODO find a way to silence the handleMessage Method while requesting to play music

    private enum States{EMPTY,BUZZIG,POLL};
    private enum Pollingtypes{PRIVATE,PUBLIC,QUICK};

    private static States state = States.EMPTY;
    private static String prefix = "!";
    private static ArrayList<Integer> poll_inputs;
    private static Pollingtypes pollingtyp;

    public static void main(String[] args) throws LoginException {
        JDABuilder jdaBuilder = JDABuilder.createDefault("Nzg2MTQ1NTI0ODA5NzI4MDAw.X9CJEg._DielBHtcMZnGsG4hqcpKV0KceA");

        JDA build = jdaBuilder.build();
        Bot b = new Bot();
        build.addEventListener(b);
        jdaBuilder.setActivity(Activity.playing("type "+prefix+"help to get help"));


    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        try{
            log.info("Received message with text: {}", event.getMessage().getContentRaw());
            handleMessage(event);

        } catch (Exception e){
            log.warn("Could not process message", e);
            event.getMessage().getChannel().sendMessage("Unexpected error occurred!").queue();
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        try{
            log.info("Received message with text: {}", event.getMessage().getContentRaw());
            handleGuildMessage(event);

        } catch (Exception e){
            log.warn("Could not process message", e);
            event.getMessage().getChannel().sendMessage("Unexpected error occurred!").queue();
            e.printStackTrace();
        }
    }

    private void handleGuildMessage(GuildMessageReceivedEvent event) {

        String[] content = event.getMessage().getContentRaw().split(" ");
        prefix = "!";
        if (content[0].startsWith(prefix)){

            switch (content[0]) {
                case("!play"): {
                    Sound.getInstance().loadAndPlay(event.getChannel(), content[1]);
                }

                case("!skip"): {
                    Sound.getInstance().skipTrack(event.getChannel());
                }
            }
            super.onGuildMessageReceived(event);
        }
    }

    private void handleMessage(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        String[] content = message.getContentRaw().split(" ");
        String command = content[0];
        prefix = "!";

        if(command.startsWith(prefix)) {
            command = command.toLowerCase(Locale.ROOT).replace(prefix, "");

            switch (command) {
                case ("help"): {
                    CommandsService.getInstance().helpRequired(channel);
                    break;
                }

                case ("hellothere"): {
                    GreetingService.getInstance().greetRequired(channel);
                    break;
                }

                case ("creategreeting"): {
                    GreetingService.getInstance().createGreeting(message, channel);
                    break;
                }

                case ("changeprefix"): {
                    if(content.length>2)
                        prefix = content[1];
                    else
                        channel.sendMessage("please mind the syntax "+prefix+"changeprefix newPrefix").queue();
                    break;
                }

                case ("startbuzzer"): {
                    //TODO select if with or without buzzer sound.
                    channel.sendMessage("stated Buzzering").queue();
                    state = States.BUZZIG;
                    break;
                }

                case ("buzz"): {
                    if (state == States.BUZZIG) {
                        state = States.EMPTY;
                        channel.sendMessage(event.getAuthor().getName() + " was first to buzzer! ").queue();
                        //TODO play buzzer sound
                    }
                    else
                    {
                        channel.sendMessage("there is no buzzer to press").queue();
                    }
                    break;
                }

                case ("pud"):
                case ("plotuglydiagram"): {
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
                    File file = new File("src/main/resources/misc/dataoutput.png");

                    try {
                        ImageIO.write(bufferedImage, "png", file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //send the Diagram
                    try {
                        channel.sendFile(file).queue();
                    } catch (Exception e) {
                        channel.sendMessage("Unexpected Error ocurred. Please Check the logs").queue();
                        log.error("Unexpected Error ocurred");
                        e.printStackTrace();
                    }

                    break;
                }

                case ("pd"):
                case ("plotdiagram"): {
                    if(content.length>1) {

                        try {
                            Process p = Runtime.getRuntime().exec("python src/main/python/plotter.py " + content[1]);
                            BufferedReader errorinput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                            String errorline;
                            while ((errorline = errorinput.readLine()) != null) {
                                log.error("Python Error occurred: " + errorline);
                                channel.sendMessage("Python Error occurred: " + errorline).queue(); //Test purpose

                            }
                            errorinput.close();

                            //send the Diagram
                            if(content[1].compareTo("raw")==0)
                            {
                                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                                String line;
                                String buff="";
                                while ((line = input.readLine()) != null) {
                                    buff+=line+"\n";
                                }
                                input.close();
                                channel.sendMessage(buff).queue();
                            }
                            else {
                                File file = new File("src/main/resources/misc/dataoutput.png");
                                channel.sendFile(file).queue();
                            }

                        } catch (IOException e) {
                            channel.sendMessage("Unexpected Error occurred. Please Check the logs").queue();
                            log.error("Unexpected Error occurred: ");
                            e.printStackTrace();
                        }
                    }
                    else
                        channel.sendMessage("Error: Please mind the syntax").queue();
                    break;
                }
                case ("startpoll"): {
                    if(content.length>1)
                    {
                        state = States.POLL;
                        switch (content[1])
                        {
                            case ("private"): {
                                pollingtyp = Pollingtypes.PRIVATE;
                                break;
                            }
                            case ("public"): {
                                pollingtyp = Pollingtypes.PUBLIC;
                            }
                                break;
                            case ("quick"): {
                                pollingtyp = Pollingtypes.QUICK;
                                break;
                            }
                            default: {
                                channel.sendMessage("Error: this polling-typ is not known").queue();
                            }

                        }
                        String[] possibilitys = new String[content.length-2];
                        String output = "";
                        for (int i=0; i < possibilitys.length; i++)
                        {
                            possibilitys[i]=content[i+2];
                            output += i+": "+possibilitys[i]+"\n";
                        }
                        channel.sendMessage("----\n"+output+"\n----").queue();
                    }
                    else
                        channel.sendMessage("Error: Please mind the syntax").queue();
                    break;
                }
                case ("poll"): {
                    if(state == States.POLL) {
                        if (content.length > 1) {
                            poll_inputs.add(Integer.valueOf(content[1]));
                            if(pollingtyp == Pollingtypes.PRIVATE)
                            {

                            }
                        } else
                            channel.sendMessage("Error: Please mind the syntax").queue();
                    }
                    else
                        channel.sendMessage("Please start a poll first").queue();
                    break;
                }
                case ("inputdata"):
                {
                    if(content.length>1) {
                        try {
                            FileWriter writer = new FileWriter("src/main/resources/misc/data.txt");
                            for (int i=1;i<content.length;i++)
                            {
                                String[] s = content[i].split(":");
                                writer.write(s[0]+" "+s[1]+"\n");
                            }

                            writer.close();
                            channel.sendMessage("Saved data").queue();

                        }
                        catch (IOException e) {
                            channel.sendMessage("Unexpected Error occurred. Please Check the logs").queue();
                            log.error("Unexpected Error occurred: ");
                            e.printStackTrace();
                        }

                    }
                    else
                        channel.sendMessage("Error: Please mind the syntax").queue();
                    break;
                }

                default: {
                    channel.sendMessage("Invalid Command, type "+prefix+"help for helpy stuff").queue();
                    break;
                }

            }
        }
    }
}