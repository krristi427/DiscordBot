package services.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataObjects.Command;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import services.Service;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class CommandsStorageService extends Service {

    public static final String FILE_NAME = "src/main/resources/json/commands/1-general.json";
    public static final Path FILE_NAME_PATH = Paths.get(FILE_NAME);

    private static final CommandsStorageService instance = new CommandsStorageService();
    public static CommandsStorageService getInstance() {
        return instance;
    }

    private final Gson gson = new Gson();
    private @Getter List<Command> commands;

    private CommandsStorageService() {
        if(Files.exists(FILE_NAME_PATH)){
            readCommandsFromFile(FILE_NAME);
        } else {
            commands = new ArrayList<>();
        }
    }

    protected List<Command> readCommandsFromFile(String filePath) {
        try {
            commands = gson.fromJson(Files.readString(Path.of(filePath)), new TypeToken<ArrayList<Command>>(){}.getType());
        } catch (IOException e) {
            log.error("Couldn't get the Greetings...creating a new empty greetingList");
            e.printStackTrace();
            commands = new ArrayList<>();
        }

        return commands;
    }

    protected List<String> readCommandsFromFolder() {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.blue);

        List<String> jsonFilePaths = null;

        try (Stream<Path> walk = Files.walk(Path.of(CommandsService.FILE_NAME))){

            //go through all files depth-first and map the paths to strings
            jsonFilePaths = walk.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonFilePaths;
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
