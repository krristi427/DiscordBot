package services.reactions;

import net.dv8tion.jda.api.entities.MessageChannel;
import services.Service;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class ReactionService extends Service {

    public ReactionService() {
        super();
    }

    public String startNumberedReactionRollEvent(String[] content, MessageChannel messageChannel) {

        ArrayList<String> roles = new ArrayList<>(Arrays.asList(content).subList(2, content.length));
        String message = "";

        try {
            RoleService.getInstance().startNumberedReactionRoleEvent(roles, content[1], messageChannel);

        } catch (RoleService.WrongNumberOfRolesException e) {
            message = "Sorry but its only possible to have 10 different roles for a numbered reaction role event.\n" + e.value;
        }

        return message;
    }
}
