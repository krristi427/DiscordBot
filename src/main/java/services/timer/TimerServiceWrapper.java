package services.timer;

import net.dv8tion.jda.api.entities.MessageChannel;
import services.Wrapper;

public class TimerServiceWrapper extends TimerService implements Wrapper {

    private static final TimerServiceWrapper instance = new TimerServiceWrapper();

    public static TimerServiceWrapper getInstance() {
        return instance;
    }

    public TimerServiceWrapper() {
        super();
    }

    public void settimer(String[] content, MessageChannel channel) {

        long delay = Long.parseLong(content[1]);
        setTimer(delay, channel);
    }
}
