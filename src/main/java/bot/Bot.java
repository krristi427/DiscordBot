package bot;

import com.github.ygimenez.exception.InvalidHandlerException;
import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.PaginatorBuilder;
import dataObjects.RegisterEntry;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import services.Observer;
import services.Subject;
import services.Wrapper;
import services.poll.PollingService;
import services.reactions.ReactionHandelingService;
import services.reactions.RoleService;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Bot extends ListenerAdapter implements Subject {

    private static Properties properties = new Properties();
    private static ArrayList<RegisterEntry> commandRegister = new ArrayList<>();

    private enum States{EMPTY,BUZZIG,POLL};
    private static States state = States.EMPTY;
    private static String prefix;
    private static int standardMessageColour;

    PollingService pollingService = new PollingService();
    ReactionHandelingService reactionHandelingService = new ReactionHandelingService();

    public List<Observer> observers = new ArrayList<>();

    private ArrayList<String> ignoreNameList = new ArrayList<>();
    public User eventAuthor;

    /**
     * Method to initialize all Wrappers, so that they can notify the bot of their existence
     */
    private static void wakeUp() { //wake me up before you go go

        //needs folder boi
        Reflections reflections = new Reflections("services");
        Set<Class<? extends Wrapper>> wrapperTypes = reflections.getSubTypesOf(Wrapper.class);

        wrapperTypes.forEach(aClass -> {
            try {
                Wrapper wrapper = aClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void registerObserver(Observer observer) {

        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {

        observers.remove(observer);
    }

    public void fillObservers() {

        //needs folder boi
        Reflections reflections = new Reflections("services");
        Set<Class<? extends Observer>> observerTypes = reflections.getSubTypesOf(Observer.class);

        observerTypes.forEach(aClass -> {
            try {
                Observer o = (Observer) Class.forName(aClass.getName()).getDeclaredConstructor().newInstance();
                observers.add(o);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void notifyObservers(MessageReceivedEvent event) {
        observers.forEach(observer -> observer.update(event));
    }

    @Override
    public void notifyObservers(GuildMessageReceivedEvent event) {
        observers.forEach(observer -> observer.update(event));
    }

    @Override
    public void notifyObservers(MessageReactionAddEvent event) {
        observers.forEach(observer -> observer.update(event));
    }

    @Override
    public void notifyObservers(MessageReactionRemoveEvent event) {
        observers.forEach(observer -> observer.update(event));
    }

    //this Class is a big Singleton
    private static final Bot instance = new Bot();

    public static Bot getInstance() {
        return instance;
    }

    private Bot() {

    }

    public void register(RegisterEntry entry) {
        commandRegister.add(entry);
    }

    private static void loadGlobalProperties()
    {
        prefix = properties.getProperty("prefix");
        standardMessageColour = Integer.parseInt(properties.getProperty("standardMessageColour"), 16);
    }

    public static void main(String[] args) throws LoginException {

        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadGlobalProperties();

        String token = properties.getProperty("token");
        JDABuilder jdaBuilder = JDABuilder.createDefault(token);
        JDA build = jdaBuilder.build();

        Bot b = getInstance();

        //this must be here tho
        try {
            Pages.activate(PaginatorBuilder.createSimplePaginator(build));
        } catch (InvalidHandlerException e) {
            e.printStackTrace();
        }

        build.addEventListener(b);
        jdaBuilder.setActivity(Activity.playing("type "+ Bot.prefix +"help to get help"));
        b.fillObservers();

        wakeUp();
    }

    public String getPrefix(){
        return prefix;
    }

    public void sendMessage(String message, String title,  int color, MessageChannel channel) //sends a custom Message
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setTitle(title);
        info.setColor(color);
        info.setDescription(message);
        channel.sendMessage(info.build()).queue();
    }

    public void sendMessage(String message, String title, String imageUrl, MessageChannel channel) //message with image
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setTitle(title);
        info.setColor(standardMessageColour);
        info.setDescription(message);
        info.setImage(imageUrl);
        channel.sendMessage(info.build()).queue();
    }

    public void sendMessage(String message, String title, MessageChannel channel)
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setTitle(title);
        info.setColor(standardMessageColour);
        info.setDescription(message);
        channel.sendMessage(info.build()).queue();
    }

    public void sendMessage(String message, int color, MessageChannel channel)
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(color);
        info.setDescription(message);
        channel.sendMessage(info.build()).queue();
    }

    public void sendMessage(String message, MessageChannel channel) //Send a standard Message
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(standardMessageColour);
        info.setDescription(message);
        channel.sendMessage(info.build()).queue();
    }

    /**
    Info Messages are Messages that inform about an importat Information
    */
    public void sendInfoMessage(String message, MessageChannel channel)
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(0xf7ef02);
        info.setDescription(message);
        channel.sendMessage(info.build()).queue();
    }

    public void sendErrorMessage(String message, MessageChannel channel) //A Error Message is a Message to Inform the userabout an Error that Occourt (f.e Wrong arguments)
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(0xf71302);
        info.setDescription(message);
        channel.sendMessage(info.build()).queue();
    }

    public void sendTextMessage(String message, MessageChannel channel) //A Text Message is longer Text that isnt hard coded (f.e Userinput or Intertsources)
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(0x021ff7);
        info.setDescription(message);
        channel.sendMessage(info.build()).queue();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        try {
            handleAllMessages(event.getAuthor(), event.getMessage(), event.getChannel());

        } catch (Exception e){
            sendErrorMessage("Unexpected fatal error occurred!",event.getMessage().getChannel());
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {

        try{
            handleReactionAdd(event);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event){
        try{
            handleReactionRemove(event);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleReactionRemove(MessageReactionRemoveEvent event){
        //Its not suported that bots remove Reactions TODO think about if that is a good idea. It is possible in a eatch ?
        //if (event.getMember().getUser().equals(event.getChannel().retrieveMessageById(event.getMessageId()).complete().getAuthor())) return;
        MessageChannel channel = event.getChannel();
        //Attention Important: This Service handles all reactions that are based on what channel we are in or what type of message this is.
        try {
            reactionHandelingService.handleRemove(event);
        }
        catch (RoleService.MassageNotFoundException e){
            e.printStackTrace();
        }
    }

    private void handleReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        if (event.getMember().getUser().equals(event.getChannel().retrieveMessageById(event.getMessageId()).complete().getAuthor())) return;
        MessageChannel channel = event.getChannel();
        //Attention Important: This Servic handels all reactions that are based on what chanel we are in or what type of message this is.
        try {
            reactionHandelingService.handleAdd(event);
        }
        catch (RoleService.MassageNotFoundException e){
            e.printStackTrace();
        }
    }

    private void handleAllMessages(User author, Message message, MessageChannel channel) {

        if (author.isBot()) return;

        eventAuthor = author;

        String[] content = message.getContentRaw().split(" ");
        String commandWithPrefix = content[0];

        if(commandWithPrefix.startsWith(prefix)) {
            final String command = commandWithPrefix.toLowerCase(Locale.ROOT).replace(prefix, "");
            if(ignoreNameList.contains(message.getAuthor().getName())) { //ignores User in list
                sendMessage("Mit dir rede ich nicht\nMimimimimimimimimi", channel);
                return;
            }

            RegisterEntry registerEntry = commandRegister.stream()
                    .filter(entry -> entry.getCommand().equals(command))
                    .collect(Collectors.toList())
                    .get(0);

            try {
                registerEntry.getMethod().invoke(registerEntry.getService(), content, channel);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(MessageReceivedEvent event) {

        /*if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        String[] content = message.getContentRaw().split(" ");
        String command = content[0];

        if(command.startsWith(prefix)) {
            command = command.toLowerCase(Locale.ROOT).replace(prefix, "");

            if(ignoreNameList.contains(message.getAuthor().getName())) { //ignores User in list
                sendMessage("Mit dir rede ich nicht", channel);
                return;
            }

            switch (command) {
                //BEGIN DefaultServices

                case ("changeprefix"): {
                    if(content.length>1)
                    {
                        prefix = content[1];
                        properties.setProperty("prefix", prefix);

                        sendInfoMessage("Prefix changed to: "+ Bot.prefix,channel);
                    }
                    else
                        sendErrorMessage("please mind the syntax: "+prefix+"changeprefix newPrefix",channel);
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
                                sendErrorMessage("Error: The bot.Bot has does not have enough rights for this polling-typ! Falling back to Public",channel);
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
                case("starteactionrolls"):
                case("srr"):
                {
                    if(content.length>1)
                    {
                        ArrayList<String> rolls=new ArrayList<>();
                        ArrayList<String> rollEmojis=new ArrayList<>();
                        String[] split;
                        for(int i=2; i<content.length;i++) {
                            split = content[i].split(":");
                            rolls.add(split[0]);
                            rollEmojis.add(split[1]);
                        }
                        try {
                            RollService.getInstance().startPersonalReactionRollEvent(rolls,rollEmojis,content[1],message.getAuthor().getName(),channel);
                        } catch (RollService.WrongNumberOfRollsException e) {
                            sendErrorMessage("Sorry but its only possible to have 10 different rolls for a numbered reaction roll event.\n"+e.value,channel);

                        }
                    }
                    else
                        sendErrorMessage("Error: Please mind the syntax",channel);
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

                case ("ignore"):
                {
                    if(content.length>1)
                    {
                        String name = content[1];

                        if(!event.getMember().hasPermission(Permission.BAN_MEMBERS)) //TODO wird so geändert, daskein admin ignoriert werden darf (schaffe es nicht einen Member anhand seines Namens zu fidnen).
                            name = message.getAuthor().getName();
                        ignoreNameList.add(name);
                        sendInfoMessage(name+" wird ab jetzt ignoriert",channel);
                    }
                    else
                        sendErrorMessage("Error: Please mind the syntax",channel);
                    break;
                }

                case ("disignore"):
                {
                    if(content.length>1)
                    {
                        String name = content[1];
                        ignoreNameList.remove(name);
                        sendInfoMessage(name+" wird nicht länger ignoriert",channel);
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
        }*/
    }
}