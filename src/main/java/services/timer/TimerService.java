package services.timer;

import net.dv8tion.jda.api.entities.MessageChannel;
import services.Service;

import java.util.Timer;
import java.util.TimerTask;

public abstract class TimerService extends Service {

    public TimerService() {
        super();
    }

    protected void setTimer(long delay, MessageChannel channel) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                bot.sendInfoMessage("Timer ran out!", channel);
            }
        }, delay);
    }
}
