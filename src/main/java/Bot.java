import dataObjects.Poll;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import services.audio.Sound;
import services.authorisation.AuthorisationService;
import services.commands.CommandsService;
import services.greeting.GreetingService;
import services.joke.JokeService;
import services.plotting.PlottingService;
import services.poll.PollingService;
import services.reactionHandelingService.ReactionHandelingService;
import services.roll.RollService;


import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
public class Bot extends ListenerAdapter {

    //TODO find a way to silence the handleMessage Method while requesting to play music

    private enum States{EMPTY,BUZZIG,POLL};


    private static States state = States.EMPTY;
    private static String prefix = "!";

    PollingService pollingService = new PollingService();
    ReactionHandelingService reactionHandelingService = new ReactionHandelingService();


    public static void main(String[] args) throws LoginException {
        JDABuilder jdaBuilder = JDABuilder.createDefault("Nzg2MTQ1NTI0ODA5NzI4MDAw.X9CJEg._DielBHtcMZnGsG4hqcpKV0KceA");
        //JDABuilder jdaBuilder = JDABuilder.createDefault("Nzk3ODMzMDcwMDEwMTcxNDMy.X_sN8g._JfC9VomIR-qoZ1LMc-c0uxGP0c");

        JDA build = jdaBuilder.build();
        Bot b = new Bot();
        build.addEventListener(b);
        jdaBuilder.setActivity(Activity.playing("type "+prefix+"help to get help"));
    }

    private void sendMessage(String message, String title,  int color, MessageChannel channel)
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setTitle(title);
        info.setColor(color);
        info.setDescription(message);
        channel.sendMessage(info.build()).queue();
    }
    private void sendMessage(String message, String title, MessageChannel channel)
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setTitle(title);
        info.setColor(0xf45642);
        info.setDescription(message);
        channel.sendMessage(info.build()).queue();
    }
    private void sendMessage(String message, int color, MessageChannel channel)
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(color);
        info.setDescription(message);
        channel.sendMessage(info.build()).queue();
    }
    private void sendMessage(String message, MessageChannel channel)
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(0xf45642);
        info.setDescription(message);
        channel.sendMessage(info.build()).queue();
    }
    private void sendInfoMessage(String message, MessageChannel channel)
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(0xf7ef02);
        info.setDescription(message);
        channel.sendMessage(info.build()).queue();
    }
    private void sendErrorMessage(String message, MessageChannel channel)
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(0xf71302);
        info.setDescription(message);
        channel.sendMessage(info.build()).queue();
    }
    private void sendTextMessage(String message, MessageChannel channel)
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(0x021ff7);
        info.setDescription(message);
        channel.sendMessage(info.build()).queue();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        try{
            log.info("Received message with text: {}", event.getMessage().getContentRaw());
            handleMessage(event);

        } catch (Exception e){
            log.warn("Could not process message", e);
            sendErrorMessage("Unexpected fatal error occurred!",event.getMessage().getChannel());
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        try{
            log.info("Received message with text: {}", event.getMessage().getContentRaw());
            handleGuildMessage(event);

        } catch (Exception e){
            log.warn("Could not process message", e);
            event.getMessage().getChannel().sendMessage("Unexpected error occurred!").queue();
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {

        try{
            log.info("Received message with text: {}", event.toString());
            handleReactionMessage(event);

        } catch (Exception e){
            log.warn("Could not process message", e);
            e.printStackTrace();
        }
    }

    private void handleReactionMessage(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        if (event.getMember().getUser().equals(event.getChannel().retrieveMessageById(event.getMessageId()).complete().getAuthor())) return;
        MessageChannel channel = event.getChannel();
        //Attention Imported: This Servic handels all reactions that are based on what chanel we are in or what type of message this is.
        reactionHandelingService.handel(event);


    }

    private void handleGuildMessage(GuildMessageReceivedEvent event) {


        String[] content = event.getMessage().getContentRaw().split(" ");
        String command = content[0];
        if(command.startsWith(prefix)) {
            command = command.toLowerCase(Locale.ROOT).replace(prefix, "");

            switch (command) {
                case ("play") -> {
                    Sound.getInstance().loadAndPlay(event.getChannel(), content[1]);
                    break;
                }
                case ("pause") -> {
                    Sound.getInstance().pauseTrack(event.getChannel());
                    break;
                }
                case ("resume") -> {
                    Sound.getInstance().resumeTrack(event.getChannel());
                    break;
                }
                case ("stop") -> {
                    Sound.getInstance().stopPlaying(event.getChannel());
                    break;
                }
                case ("currentQueue") -> {
                    Sound.getInstance().currentQueue(event.getChannel());
                    break;
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

        if(command.startsWith(prefix)) {
            command = command.toLowerCase(Locale.ROOT).replace(prefix, "");

            switch (command) {
                //BEGIN DefaultServices
                case ("help"): {
                    CommandsService.getInstance().helpRequired(channel,prefix);
                    break;
                }

                case ("changeprefix"): {
                    if(content.length>1)
                    {
                        prefix = content[1];
                        sendInfoMessage("Prefix changed to: "+prefix,channel);
                    }
                    else
                        sendErrorMessage("please mind the syntax: "+prefix+"changeprefix newPrefix",channel);
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
                    sendInfoMessage("stated Buzzering",channel);
                    state = States.BUZZIG;
                    break;
                }

                case ("buzz"): {
                    if (state == States.BUZZIG) {
                        state = States.EMPTY;
                        sendTextMessage(event.getAuthor().getName() + " was first to buzzer! ",channel);
                        //TODO play buzzer sound
                    }
                    else
                    {
                        sendErrorMessage("there is no buzzer to press",channel);
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
                            sendErrorMessage("Unexpected Error occurred. Please Check the logs",channel);
                            log.error("Unexpected Error occurred: ");
                            e.printStackTrace();
                        }

                    }
                    else
                        sendErrorMessage("Error: Please mind the syntax",channel);
                    break;
                }

                case ("pud"):
                case ("plotuglydiagram"): {
                    try {
                        PlottingService.getInstance().plotugly(channel);
                    } catch (IOException e) {
                        sendErrorMessage("Unexpected Error occurred. Please Check the logs",channel);
                        log.error("Unexpected Error ocurred");
                        e.printStackTrace();
                    }
                    break;
                }

                case ("pd"):
                case ("plot"):
                case ("plotdiagram"): {
                    if(content.length>1) {

                        try {
                            PlottingService.getInstance().plot(content,channel);

                        } catch (IOException  e) {
                            sendErrorMessage("Unexpected Error occurred. Please Check the logs",channel);
                            log.error("Unexpected Error occurred: ");
                            e.printStackTrace();
                        }
                        catch (PlottingService.PythonError e) {
                            log.error(e.errorLines);
                        }
                    }
                    else
                        sendErrorMessage("Error: Please mind the syntax",channel);
                    break;
                }

                //BEGIN PollingServices //TODO move Plotting functions to service
                case ("startpoll"):
                {

                    if(content.length>1)
                    {
                        pollingService.startpoll(content,channel);
                    }
                    else
                        sendErrorMessage("Error: Please mind the syntax",channel);
                    break;
                }
                case ("poll"): {
                    if(pollingService.existsActivePoll()) {
                        if (content.length > 1) {
                            try{
                                pollingService.poll(content,message);
                            }
                            catch (net.dv8tion.jda.api.exceptions.InsufficientPermissionException e)
                            {
                                sendErrorMessage("Error: The Bot has does not have enough rights for this polling-typ! Falling back to Public",channel);
                                pollingService.setActivePollingtyp(Poll.Pollingtypes.PUBLIC);
                            }
                            catch (PollingService.WrongPollingTypException e)
                            {
                                sendInfoMessage("This Command is not allowed in this Pollingtyp: "+e.pollingtype,channel);
                            }

                        } else
                            sendErrorMessage("Error: Please mind the syntax",channel);
                    }
                    else
                        sendErrorMessage("Please start a poll first",channel);
                    break;
                }

                case("activepoll"):
                {
                    try {
                        pollingService.activePoll(content,channel);
                    }
                    catch (PollingService.WrongValueException e)
                    {
                        sendErrorMessage("Error: Only Digits are allowed as first argument. But given: "+e.value,channel);
                    }

                    break;
                }

                case("endpoll"):
                {
                    try
                    {
                        pollingService.endpoll(channel);
                    }
                    catch (IOException e) {
                        sendErrorMessage("Unexpected Error occurred. Please Check the logs",channel);
                        //log.error("Unexpected Error occurred: ");
                        e.printStackTrace();
                    }
                    break;
                }

                case("admin?"):
                {
                    try {
                        AuthorisationService.getInstance().isAuthorised("admin",event.getMember());
                    } catch (IOException e) {
                        sendErrorMessage("Unexpected Error occurred. Please Check the logs",channel);
                        log.error("Unexpected Error occurred: ");
                        e.printStackTrace();
                    }
                    break;
                }
                //BEGIN RollService
                case("startnumberedreactionrolls"):
                case("snrr"):
                {
                    if(content.length>1)
                    {
                        ArrayList<String> rolls=new ArrayList<>();
                        for(int i=2; i<content.length;i++)
                            rolls.add(content[i]);
                        try {
                            RollService.getInstance().startNumberedReactionRollEvent(rolls,content[1],message.getAuthor().getName(),channel);
                        } catch (RollService.WrongNumberOfRollsException e) {
                            sendErrorMessage("Sorry but its only possible to have 10 different rolls for a numbered reaction roll event.\n"+e.value,channel);

                        }
                    }
                    else
                        sendErrorMessage("Error: Please mind the syntax",channel);
                    break;
                }

                //BEGIN JokeService
                case("joke"):
                {
                    JokeService.getInstance().getJoke(channel);
                    break;
                }

                //BEGIN MiscService
                case ("clear"):
                {
                    if(content.length>1 && (content[1].matches("-?\\d+")||content[1].equals("all")))
                    {

                        if(content[1].equals("all")) { // DO NOT USE ITS VERY BUGY AND THERE FOR LEAK INTENSIV. TODO make faster :)
                            List<Message> messages;

                            while (true){
                                 messages = channel.getHistory().retrievePast(100).complete();
                                 if(messages.isEmpty())
                                     break;
                                 for (Message msg : messages) {
                                    msg.delete().queue();

                                 }
                            };
                        }
                        else {


                            int n = Integer.parseInt(content[1])+1;
                            if (n > 100) {
                                sendErrorMessage("Can only delete 99 msg at a time", channel);
                            } else {
                                List<Message> messages = channel.getHistory().retrievePast(n).complete();
                                for (Message msg : messages) {
                                    msg.delete().queue();
                                }
                            }
                        }


                    }
                    else
                        sendErrorMessage("Error: Please mind the syntax",channel);


                    break;
                }


                //END SERVICES


                default: {
                    sendErrorMessage("Invalid Command, type "+prefix+"help for helpy stuff",channel);
                    break;
                }

            }
        }
    }
}