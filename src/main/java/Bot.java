import dataObjects.*;
import services.*;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.ArrayList;
import java.util.Locale;

@Slf4j
public class Bot extends ListenerAdapter {

    //TODO find a way to silence the handleMessage Method while requesting to play music

    private enum States{EMPTY,BUZZIG,POLL};


    private static States state = States.EMPTY;
    private static String prefix = "!";
    private static ArrayList<Poll> polls = new ArrayList<>();
    private static Poll activPoll;



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
                case ("!play") -> {
                    Sound.getInstance().loadAndPlay(event.getChannel(), content[1]);
                }
                case ("!pause") -> {
                    Sound.getInstance().pauseTrack(event.getChannel());
                }
                case ("!resume") -> {
                    Sound.getInstance().resumeTrack(event.getChannel());
                }
                case ("!stopPlaying") -> {
                    Sound.getInstance().stopPlaying(event.getChannel());
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




                //BEGIN DefaultServices
                case ("help"): {
                    CommandsService.getInstance().helpRequired(channel);
                    break;
                }

                case ("changeprefix"): {
                    if(content.length>2)
                        prefix = content[1];
                    else
                        channel.sendMessage("please mind the syntax "+prefix+"changeprefix newPrefix").queue();
                    break;
                }

                //BEGIN GreetingServices
                case ("hellothere"): {
                    GreetingService.getInstance().greetRequired(channel);
                    break;
                }

                case ("creategreeting"): {
                    GreetingService.getInstance().createGreeting(message, channel);
                    break;
                }

                //BEGIN QuizzServices
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
                //BEGIN PlottingServices
                case ("inputdata"):
                {
                    if(content.length>1) {
                        try {
                            PlottingService.getInstance().inputdata(1,content,channel);
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

                case ("pud"):
                case ("plotuglydiagram"): {
                    try {
                        PlottingService.getInstance().plotugly(channel);
                    } catch (IOException e) {
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
                            PlottingService.getInstance().plot(content,channel);

                        } catch (IOException  e) {
                            channel.sendMessage("Unexpected Error occurred. Please Check the logs").queue();
                            log.error("Unexpected Error occurred: ");
                            e.printStackTrace();
                        }
                        catch (PlottingService.PythonError e) {
                            log.error(e.errorLines);
                        }
                    }
                    else
                        channel.sendMessage("Error: Please mind the syntax").queue();
                    break;
                }

                //BEGIN PollingServices //TODO move Plotting functions to service
                case ("startpoll"):
                {
                    Poll.Pollingtypes pollingtyp = Poll.Pollingtypes.PUBLIC;
                    if(content.length>1)
                    {
                        switch (content[1])
                        {
                            case ("private"): {
                                pollingtyp = Poll.Pollingtypes.PRIVATE;
                                break;
                            }
                            case ("public"): {
                                pollingtyp = Poll.Pollingtypes.PUBLIC;
                            }
                                break;
                            case ("quick"): {
                                pollingtyp = Poll.Pollingtypes.QUICK;
                                break;
                            }
                            default: {
                                channel.sendMessage("This polling-typ is not known. Falling back to public").queue();
                                break;
                            }
                        }

                        String[] possibilitys = new String[content.length-2];
                        String output = "";
                        for (int i=0; i < possibilitys.length; i++)
                        {
                            possibilitys[i]=content[i+2];
                            output += i+": "+possibilitys[i]+"\n";
                        }
                        Poll newPoll = new Poll(pollingtyp,possibilitys);
                        polls.add(newPoll);
                        activPoll = newPoll;
                        channel.sendMessage("--Poll: "+(polls.size()-1)+" --\n"+output+"---------").queue();
                    }
                    else
                        channel.sendMessage("Error: Please mind the syntax").queue();
                    break;
                }
                case ("poll"): {
                    if(activPoll!=null) {
                        if (content.length > 1) {

                            if(activPoll.pollingtyp == Poll.Pollingtypes.QUICK)
                            {
                                channel.sendMessage("This Command is not allowed in Quickmode").queue();
                                break;
                            }

                            if(content[1].matches("\\d+"))
                                activPoll.addToPossibilitie(Integer.valueOf(content[1]));
                            else
                                activPoll.addToPossibilitie(content[1]);

                            if(activPoll.pollingtyp == Poll.Pollingtypes.PRIVATE)
                            {
                                try {
                                    message.delete().queue();
                                }
                                catch (net.dv8tion.jda.api.exceptions.InsufficientPermissionException e)
                                {
                                    channel.sendMessage("Error: The Bot has does not have enough rights for this polling-typ! Falling back to Public").queue();
                                    activPoll.pollingtyp = Poll.Pollingtypes.PUBLIC;
                                }
                            }

                        } else
                            channel.sendMessage("Error: Please mind the syntax").queue();
                    }
                    else
                        channel.sendMessage("Please start a poll first").queue();
                    break;
                }
                case("activatepoll"):
                {
                    if (content.length > 1) {
                        if(content[1].matches("\\d+")) {
                            activPoll = polls.get(Integer.valueOf(content[1]));
                            String output="";
                            String[] possibilitys = activPoll.getAnswers();
                            for (int i=0; i < possibilitys.length; i++)
                            {
                                output += i+": "+possibilitys[i]+"\n";
                            }
                            channel.sendMessage("--Poll: "+polls.indexOf(activPoll)+" --\n"+output+"---------").queue();
                        }
                        else
                            channel.sendMessage("Error: Only Digits are allowed as first argument").queue();
                    }
                    else
                        channel.sendMessage("Error: Please mind the syntax").queue();
                    break;
                }
                case("endpoll"):
                {
                    try {
                        PlottingService.getInstance().inputdata(0,activPoll.getPollingResults(),channel);
                    }
                    catch (IOException e) {
                        channel.sendMessage("Unexpected Error occurred. Please Check the logs").queue();
                        log.error("Unexpected Error occurred: ");
                        e.printStackTrace();
                    }
                    break;
                }

                //END SERVICES


                default: {
                    channel.sendMessage("Invalid Command, type "+prefix+"help for helpy stuff").queue();
                    break;
                }

            }
        }
    }
}