package services.reactions;

import net.dv8tion.jda.api.entities.MessageChannel;
import services.Wrapper;

public class ReactionServiceWrapper extends ReactionService implements Wrapper {

    private static final ReactionServiceWrapper instance = new ReactionServiceWrapper();
    public static ReactionServiceWrapper getInstance() { return instance; }

    public ReactionServiceWrapper() {
        super();
    }

    public void snrr(String[] content, MessageChannel channel) {

        if (content.length > 1 && content.length < 10) {
            String message = startNumberedReactionRollEvent(content, channel);
            bot.sendInfoMessage(message, channel);
        }
    }

    public void srr(String[] content, MessageChannel channel) {

    }
}
