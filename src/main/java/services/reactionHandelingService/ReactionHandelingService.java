package services.reactionHandelingService;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import services.Service;
import services.roll.RollService;

public class ReactionHandelingService implements Service {

    public void handleAdd(MessageReactionAddEvent event) throws RollService.MassageNotFoundException {
        //TODO finde herraus was für ein Typ die nachricht hat auf die Reagiert wurde. Also Poll oder ReactionRollEvent
        RollService.getInstance().getRoll(event.getReactionEmote().getName(),event);

    }
    public void handleRemove(MessageReactionRemoveEvent event) throws RollService.MassageNotFoundException {
        //TODO finde herraus was für ein Typ die nachricht hat auf die Reagiert wurde. Also Poll oder ReactionRollEvent
        RollService.getInstance().loseRoll(event.getReactionEmote().getName(),event);

    }
}
