package services.greeting;

import dataObjects.Greeting;
import services.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class GreetingService extends Service {

    public GreetingService() {
        super();
    }

    private final Random random = new Random();

    protected Optional<Greeting> getRandomGreeting() {

        List<Greeting> actualGreets = GreetingsStorageService.getInstance().getGreetingList();
        Greeting greeting = actualGreets.get(random.nextInt(actualGreets.size()));
        return Optional.of(greeting);
    }

    protected String createGreeting(String greetingTextFromRaw) {

        Greeting greeting = new Greeting(greetingTextFromRaw);
        GreetingsStorageService greetingsStorageService = GreetingsStorageService.getInstance();
        greetingsStorageService.getGreetingList().add(greeting);
        greetingsStorageService.storeQuestions();

        return "Created new Greeting: "
                + greetingTextFromRaw + ". To see it, just ask hello again";
    }
}
