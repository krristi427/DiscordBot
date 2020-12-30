package services.greeting;

import dataObjects.Greeting;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import java.util.List;
import java.util.Random;

public class GreetingService {

    private static final GreetingService instance = new GreetingService();
    public static GreetingService getInstance() {
        return instance;
    }
    private final Random random= new Random();

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
}
