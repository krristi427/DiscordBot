package services.reactions;

import net.dv8tion.jda.api.entities.MessageChannel;
import services.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ReactionService extends Service {

    public ReactionService() {
        super();
    }

    public String startNumberedReactionRollEvent(String[] content, MessageChannel messageChannel) {

        ArrayList<String> roles = new ArrayList<>(Arrays.asList(content).subList(2, content.length));
        String message = "";

        try {
            RollService.getInstance().startNumberedReactionRollEvent(roles, content[1], "temporary name", messageChannel);

        } catch (RollService.WrongNumberOfRollsException e) {
            message = "Sorry but its only possible to have 10 different roles for a numbered reaction roll event.\n" + e.value;
        }

        return message;
    }
}
