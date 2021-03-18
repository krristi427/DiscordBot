package services.commands;

import dataObjects.Command;
import net.dv8tion.jda.api.entities.MessageChannel;
import services.Service;

import java.util.List;


public class CommandsService {

    private static final CommandsService instance = new CommandsService();
    public static CommandsService getInstance() {
        return instance;
    }

    protected String helpRequired(MessageChannel channel, String prefix) {

        String buffer = "";
        List<Command> commands = CommandsStorageService.getInstance().getCommands();
        for (Command command : commands) {
            String name = command.getName();
            String explanation = command.getExplanation();

            buffer += (prefix+name + ": " + explanation+"\n\n");
        }
        EmbedBuilder info = new EmbedBuilder();
        info.setTitle("Help");
        info.setColor(0xff8800);
        info.setDescription(buffer);
        info.setImage("attachment://src/main/resources/icons/help.png");
        channel.sendMessage(info.build()).queue(); //restructured to send all heps at once
        info.clear();
    }

    //TODO add a master-command to add a new command to the list
}
