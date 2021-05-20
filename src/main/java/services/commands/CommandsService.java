package services.commands;

import com.github.ygimenez.model.Page;
import com.github.ygimenez.type.PageType;
import dataObjects.Command;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import services.Service;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public abstract class CommandsService extends Service {

    public static final String FILE_NAME = "src/main/resources/json/commands";
    public static final List<String> EMOTES = Arrays.asList("\uD83C\uDD98",
            "\uD83D\uDD0A",
            "\uD83D\uDD18",
            "\uD83D\uDCC8",
            "❓",
            "❗");

    public static final List<String> CONTENT = Arrays.asList("General",
            "Audio",
            "Buzz",
            "Plot",
            "Poll",
            "Reaction");

    private @Getter
    final HashMap<String, Page> pages = new HashMap<>();

    private final CommandsStorageService commandsStorageServiceInstance = CommandsStorageService.getInstance();

    CommandsService() {
        super();
    }

    protected HashMap<String, Page> helpRequired(MessageChannel channel, String prefix) {
        return getMessageMap(commandsStorageServiceInstance.readCommandsFromFolder());
    }

    private HashMap<String, Page> getMessageMap(List<String> paths) {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.blue);

        for (int i = 0; i < paths.size(); i++) {

            //read the commands in the file
            List<Command> commands = commandsStorageServiceInstance.readCommandsFromFile(paths.get(i));

            StringBuffer buffer = new StringBuffer();

            //merge the contents together
            commands.forEach(command -> buffer.append("**")
                    .append(command.getName())
                    .append("**")
                    .append(": ")
                    .append(command.getExplanation()).append("\n"));

            embedBuilder.setDescription(buffer.toString());
            embedBuilder.setTitle(CONTENT.get(i));

            pages.put(EMOTES.get(i), new Page(PageType.EMBED, embedBuilder.build()));
        }

        return getPages();
    }
}
