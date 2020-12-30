package services.greeting;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataObjects.Greeting;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GreetingsStorageService {

    public static final String FILE_NAME = "src/main/resources/json/greetings.json";
    public static final Path FILE_NAME_PATH = Paths.get(FILE_NAME);
    private static final GreetingsStorageService instance = new GreetingsStorageService();
    public static GreetingsStorageService getInstance() {
        return instance;
    }

    private final Gson gson = new Gson();
    private @Getter List<Greeting> greetingList;

    private GreetingsStorageService() {
        if(Files.exists(FILE_NAME_PATH)){
            readQuestionsFromFile();
        } else {
            greetingList = new ArrayList<>();
        }
    }

    private void readQuestionsFromFile() {
        try {
            greetingList = gson.fromJson(Files.readString(FILE_NAME_PATH), new TypeToken<ArrayList<Greeting>>(){}.getType());
        } catch (IOException e) {
            log.error("Couldn't get the Greetings...creating a new empty greetingList");
            e.printStackTrace();
            greetingList = new ArrayList<>();
        }
    }

    public void storeQuestions(){
        String questionsAsString = gson.toJson(greetingList);
        try {
            Files.writeString(FILE_NAME_PATH, questionsAsString);
        } catch (IOException e) {
            log.error("Couldn't store the questions!");
            e.printStackTrace();
        }
    }
}
