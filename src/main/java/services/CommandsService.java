package services;

import dataObjects.Command;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;


public class CommandsService {

    private static final CommandsService instance = new CommandsService();
    public static CommandsService getInstance() {
        return instance;
    }

    public void helpRequired(MessageChannel channel) {

            String buffer = "";
        List<Command> commands = CommandsStorageService.getInstance().getCommands();
        for (Command command : commands) {
            String name = command.getName();
            String explanation = command.getExplanation();

            buffer += (name + ": " + explanation+"\n");
        }
        channel.sendMessage(buffer).queue(); //restructured to send all heps at once
    }

    //TODO add a master-command to add a new command to the list
}
