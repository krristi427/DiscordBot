package services.commands;

import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.Page;
import net.dv8tion.jda.api.entities.MessageChannel;
import services.Wrapper;

import java.util.HashMap;

public class CommandsServiceWrapper extends CommandsService implements Wrapper {

    private static final CommandsServiceWrapper instance = new CommandsServiceWrapper();
    public static CommandsServiceWrapper getInstance() {
        return instance;
    }

    public CommandsServiceWrapper() {
        super();
    }

    public void help(String[] content, MessageChannel channel) {
        HashMap<String, Page> pages = helpRequired(channel, bot.getPrefix());

        channel.sendMessage("Did I hear Help? Click one of the emotes there :D").queue(success -> Pages.categorize(success, pages));

    }
}
