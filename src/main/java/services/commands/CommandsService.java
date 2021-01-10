package services.commands;

import dataObjects.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;


public class CommandsService {

    private static final CommandsService instance = new CommandsService();
    public static CommandsService getInstance() {
        return instance;
    }

    public void helpRequired(MessageChannel channel, String prefix) {

            String buffer = "";
        List<Command> commands = CommandsStorageService.getInstance().getCommands();
        for (Command command : commands) {
            String name = command.getName();
            String explanation = command.getExplanation();

            buffer += (prefix+name + ": " + explanation+"\n");
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
