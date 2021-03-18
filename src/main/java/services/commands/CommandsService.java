package services.commands;

import dataObjects.Command;
import net.dv8tion.jda.api.entities.MessageChannel;
import services.Service;

import java.util.List;


public abstract class CommandsService extends Service {

    CommandsService() {
        super();
    }

    protected String helpRequired(MessageChannel channel, String prefix) {

        String buffer = "";
        List<Command> commands = CommandsStorageService.getInstance().getCommands();
        for (Command command : commands) {
            String name = command.getName();
            String explanation = command.getExplanation();

            buffer += (prefix+name + ": " + explanation+"\n\n");
        }

        return buffer;
    }

    //TODO add a master-command to add a new command to the list
}
