package services.reactionHandelingService;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import services.roll.RollService;

public class ReactionHandelingService {

    public void handel(MessageReactionAddEvent event)
    {
        //TODO finde herraus was für ein Typ die nachricht hat auf die Reagiert wurde. Also Poll oder ReactionRollEvent
        RollService.getInstance().react(event.getReactionEmote().getName(),event);

    }

}
