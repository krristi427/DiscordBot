package services.greeting;

import dataObjects.Greeting;
import net.dv8tion.jda.api.entities.MessageChannel;
import services.Wrapper;

import java.util.Arrays;
import java.util.Optional;

public class GreetingServiceWrapper extends GreetingService implements Wrapper {

    //TODO add funcionality to delete greetings

    private static final GreetingServiceWrapper instance = new GreetingServiceWrapper();
    public static GreetingServiceWrapper getInstance() { return instance; }

    public GreetingServiceWrapper() {
        super();
    }

    public void hellothere(String[] content, MessageChannel channel) {

        Optional<Greeting> greeting = getRandomGreeting();
        greeting.ifPresent(greeting1 -> channel.sendMessage(greeting1.getGreetText()).queue());
    }

    public void creategreeting(String[] content, MessageChannel channel) {

        String[] contentCopy = Arrays.copyOfRange(content, 1, content.length);
        String greetingText = String.join(" ", contentCopy);

        channel.sendMessage(createGreeting(greetingText)).queue();
    }


}
