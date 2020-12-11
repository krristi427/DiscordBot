package services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataObjects.Command;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CommandsStorageService {

    public static final String FILE_NAME = "src/main/resources/json/commands.json";
    public static final Path FILE_NAME_PATH = Paths.get(FILE_NAME);
    private static final CommandsStorageService instance = new CommandsStorageService();
    public static CommandsStorageService getInstance() {
        return instance;
    }

    private final Gson gson = new Gson();
    private @Getter List<Command> commands;

    private CommandsStorageService() {
        if(Files.exists(FILE_NAME_PATH)){
            readCommandsFromFile();
        } else {
            commands = new ArrayList<>();
        }
    }

    private void readCommandsFromFile() {
        try {
            commands = gson.fromJson(Files.readString(FILE_NAME_PATH), new TypeToken<ArrayList<Command>>(){}.getType());
        } catch (IOException e) {
            log.error("Couldn't get the Greetings...creating a new empty greetingList");
            e.printStackTrace();
            commands = new ArrayList<>();
        }
    }

    public void storeQuestions() {
        String questionsAsString = gson.toJson(commands);
        try {
            Files.writeString(FILE_NAME_PATH, questionsAsString);
        } catch (IOException e) {
            log.error("Couldn't store the questions!");
            e.printStackTrace();
        }
    }
}
