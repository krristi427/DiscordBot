package services.reactions;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

public class ReactionHandelingService {

    public void handleAdd(MessageReactionAddEvent event) throws RoleService.MassageNotFoundException {
        //TODO finde herraus was für ein Typ die nachricht hat auf die Reagiert wurde. Also Poll oder ReactionRollEvent
        RoleService.getInstance().getRole(event.getReactionEmote().getName(),event);

    }
    public void handleRemove(MessageReactionRemoveEvent event) throws RoleService.MassageNotFoundException {
        //TODO finde herraus was für ein Typ die nachricht hat auf die Reagiert wurde. Also Poll oder ReactionRollEvent
        RoleService.getInstance().loseRole(event.getReactionEmote().getName(),event);

    }
}
