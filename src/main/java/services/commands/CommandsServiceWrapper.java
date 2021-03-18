package services.commands;

import net.dv8tion.jda.api.entities.MessageChannel;
import services.Wrapper;

public class CommandsServiceWrapper extends CommandsService implements Wrapper {

    private static final CommandsServiceWrapper instance = new CommandsServiceWrapper();
    public static CommandsServiceWrapper getInstance() {
        return instance;
    }

    public CommandsServiceWrapper() {
        super();
    }

    public void help(String[] content, MessageChannel channel) {
        String buffer = helpRequired(channel, bot.getPrefix());
        bot.sendMessage(buffer,"Help",0xff8800, channel);
    }
}
