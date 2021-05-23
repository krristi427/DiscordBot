package services.joke;

import net.dv8tion.jda.api.entities.MessageChannel;
import services.Wrapper;

import java.util.concurrent.ExecutionException;

public class JokeServiceWrapper extends JokeService implements Wrapper {

    private static final JokeServiceWrapper instance = new JokeServiceWrapper();
    public static JokeServiceWrapper getInstance() { return instance; }

    public JokeServiceWrapper() {
        super();
    }

    public void joke(String[] content, MessageChannel channel) throws InterruptedException, ExecutionException {
        String message;
        if (content.length>1) {
            message = getJokeFromQuery(content[1]).get();
        } else {
            message = getJoke().get();
        }
        if(!message.equals("")) {
            bot.sendMessage(message, "**Dead Jokes**", channel);
        }
    }
}
