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

        List<Command> commands = CommandsStorageService.getInstance().getCommands();
        commands.forEach(command -> {
            String name = command.getName();
            String explanation = command.getExplanation();

            channel.sendMessage(name + ": " + explanation).queue();
        });
    }

    //TODO add a master-command to add a new command to the list
}
