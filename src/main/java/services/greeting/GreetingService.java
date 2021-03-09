package services.greeting;

import dataObjects.Greeting;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import services.Observer;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GreetingService implements Observer {

    private static final GreetingService instance = new GreetingService();
    public static GreetingService getInstance() {
        return instance;
    }
    private final Random random= new Random();

    //as per Singleton, each class must have a private constructor
    private GreetingService() {}

    public void greetRequired(MessageChannel channel) {

        Greeting greeting = getRandomGreeting(channel);
        if(greeting == null){
            return;
        }

        channel.sendMessage(greeting.getGreetText()).queue();
    }

    private Greeting getRandomGreeting(MessageChannel channel) {

        List<Greeting> actualGreets = GreetingsStorageService.getInstance().getGreetingList();
        return actualGreets.get(random.nextInt(actualGreets.size()));
    }

    public void createGreeting(Message message, MessageChannel messageChannel) {

        String greetingTextFromRaw = message.getContentRaw().substring(16);
        Greeting greeting = new Greeting(greetingTextFromRaw);
        GreetingsStorageService greetingsStorageService = GreetingsStorageService.getInstance();
        greetingsStorageService.getGreetingList().add(greeting);
        greetingsStorageService.storeQuestions();

        messageChannel.sendMessage("Created new Greeting: "
                                        + greetingTextFromRaw + ". To see it, just ask hello again").queue();
    }


    @Override
    public void update(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        String[] content = message.getContentRaw().split(" ");
        String command = content[0];

        if(command.startsWith("!")) {
            command = command.toLowerCase(Locale.ROOT).replace("!", "");

            switch (command) {
                case ("hellothere") -> greetRequired(channel);
                case ("creategreeting") -> createGreeting(message, channel);
            }
        }
    }

    @Override
    public void update(GuildMessageReceivedEvent event) {

    }

    @Override
    public void update(MessageReactionAddEvent event) {

    }

    @Override
    public void update(MessageReactionRemoveEvent event) {

    }
}
