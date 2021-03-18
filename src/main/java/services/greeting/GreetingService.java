package services.greeting;

import dataObjects.Greeting;
import services.Service;

import java.util.List;
import java.util.Random;

public abstract class GreetingService extends Service {

    public GreetingService() {
        super();
    }

    private final Random random = new Random();

    protected Greeting getRandomGreeting() {

        //TODO return Optional to reduce null-checks
        List<Greeting> actualGreets = GreetingsStorageService.getInstance().getGreetingList();
        return actualGreets.get(random.nextInt(actualGreets.size()));
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
