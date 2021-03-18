package services.poll;

import dataObjects.Poll;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class PollingService {

    private static ArrayList<Poll> polls = new ArrayList<>();
    private static Poll activPoll;

    public void startpoll(@NotNull String[] content, @NotNull MessageChannel channel)
    {
        Poll.Pollingtypes pollingtyp = Poll.Pollingtypes.PUBLIC;
        switch (content[1])
        {
            case ("private"): {
                pollingtyp = Poll.Pollingtypes.PRIVATE;
                break;
            }
            case ("public"): {
                pollingtyp = Poll.Pollingtypes.PUBLIC;
            }
            break;
            case ("quick"): {
                pollingtyp = Poll.Pollingtypes.QUICK;
                break;
            }
            default: {
                channel.sendMessage("This polling-typ is not known. Falling back to public").queue();
                break;
            }
        }

        String[] possibilitys = new String[content.length-2];
        for (int i=0; i < possibilitys.length; i++)
        {
            possibilitys[i]=content[i+2];
        }
        Poll newPoll = new Poll(pollingtyp,possibilitys);
        newPoll.printPoll(channel,polls.size()-1);
        polls.add(newPoll);
        activPoll = newPoll;
    }

    public void poll(@NotNull String[] content, Message message) throws net.dv8tion.jda.api.exceptions.InsufficientPermissionException, WrongPollingTypException {
        if(activPoll.pollingtyp == Poll.Pollingtypes.QUICK)
        {
            throw new WrongPollingTypException(activPoll.pollingtyp);
        }

        if(content[1].matches("\\d+"))
            activPoll.addToPossibilitie(Integer.valueOf(content[1]));
        else
            activPoll.addToPossibilitie(content[1]);

        if(activPoll.pollingtyp == Poll.Pollingtypes.PRIVATE)
        {
            message.delete().queue();
        }

    }

    public void activePoll(@NotNull String[] content, @NotNull MessageChannel channel) throws WrongValueException {

        if(content.length>1)
        {
            if (content[1].matches("\\d+")) {
                activPoll = polls.get(Integer.valueOf(content[1]));
            }
            else
            {
                throw new WrongValueException(content[1]);
            }
        }
        activPoll.printPoll(channel, polls.indexOf(activPoll));
    }

    public void endpoll( @NotNull MessageChannel channel) throws IOException {
        //PlottingService.getInstance().inputdata(0,activPoll.getPollingResults(),channel);
    }

    public boolean existsActivePoll()
    {
        return activPoll!=null;
    }

    public void setActivePollingtyp(Poll.Pollingtypes typ)
    {
        activPoll.pollingtyp = typ;
    }

    public class WrongPollingTypException extends Exception
    {
        public Poll.Pollingtypes pollingtype;
        WrongPollingTypException(Poll.Pollingtypes typ)
        {
            pollingtype = typ;
        }
    }

    public class WrongValueException extends Exception
    {
        public String value;
        WrongValueException(String value)
        {
            this.value = value;
        }
    }
}
