package services.timer;

import net.dv8tion.jda.api.entities.MessageChannel;
import services.Wrapper;

import java.util.Arrays;

public class TimerServiceWrapper extends TimerService implements Wrapper {

    private static final TimerServiceWrapper instance = new TimerServiceWrapper();

    public static TimerServiceWrapper getInstance() {
        return instance;
    }

    public TimerServiceWrapper() {
        super();
    }

    public void settimer(String[] content, MessageChannel channel) {
        setTimer(getDelay(content[1]), channel);
    }

    //you feed the right values in the function, according to the unit
    private long getDelay(String delayContent) {

        char unit = delayContent.charAt(delayContent.length() - 1);
        delayContent = delayContent.substring(0, delayContent.length() - 1);

        //the multiplication with 1000 is due to the normal delay given in ms
        return switch (unit) {
            case 's' -> getDelayValue(delayContent, 1000);
            case 'm' -> getDelayValue(delayContent, 60 * 1000);
            case 'h' -> getDelayValue(delayContent, 60 * 60 * 1000);
            default -> 0;
        };
    }

    /**
     *
     * @param delayContent the String in the form hh:mm OR mm:ss
     * @param unit the exact unit derived from #getDelayUnit
     * @return the total delay to be given to the TimerTask
     */
    private long getDelayValue(String delayContent, long unit) {

        String[] parts = delayContent.split(":");

        long[] delays = Arrays.stream(parts)
                .mapToLong(Long::parseLong)
                .toArray();

        return unit * (delays[0] + delays[1]/60);
    }
}
