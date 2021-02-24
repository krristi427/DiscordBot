package services.roll;

import dataObjects.ReactionRollEvent;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import services.greeting.GreetingService;

import java.util.ArrayList;
import java.util.Collections;

public class RollService {

    ArrayList<ReactionRollEvent> events = new ArrayList<>();
    private static final RollService instance = new RollService();
    public static RollService getInstance() {
        return instance;
    }
    public void roll( @NotNull MessageChannel channel)
    {
        channel.sendMessage("https://www.youtube.com/watch?v=2ocykBzWDiM&feature=emb_title").queue();
        //well yes it dose whats intended to do.
    }

    public void startPersonalReactionRollEvent( @NotNull ArrayList<String> rolls, ArrayList<String> rollEmojis, String authorsName, String name, MessageChannel channel) throws WrongNumberOfRollsException {
        if(rolls.size()!=rollEmojis.size()||rolls.size()<1)
            throw new WrongNumberOfRollsException("rolls.size: "+rolls.size()+" or rollEmojis.size: "+rollEmojis.size()+" was unexpected");
        ReactionRollEvent event = new ReactionRollEvent(rolls,rollEmojis,name,authorsName);
        events.add(event);
        event.printEvent(channel);
    }

    public void startNumberedReactionRollEvent( @NotNull ArrayList<String> rolls, String name, String authorsName, MessageChannel channel) throws WrongNumberOfRollsException {
        if(rolls.size()>10||rolls.size()<1)
            throw new WrongNumberOfRollsException("rolls.size: "+rolls.size()+" was unexpected");
        ArrayList<String> rollEmojis = new ArrayList<>();
        switch (rolls.size())
        {
            case (10):
                rollEmojis.add("\uD83D\uDD1F");
            case (9):
                rollEmojis.add("9⃣");
            case(8):
                rollEmojis.add("8⃣");
            case(7):
                rollEmojis.add("7⃣");
            case(6):
                rollEmojis.add("6⃣");
            case(5):
                rollEmojis.add("5⃣");
            case(4):
                rollEmojis.add("4⃣");
            case(3):
                rollEmojis.add("3⃣");
            case(2):
                rollEmojis.add("2️⃣");
            case(1):
                rollEmojis.add("1️⃣");
        } //well yes that's right because of ne breaks;
        Collections.reverse(rollEmojis);
        startPersonalReactionRollEvent(rolls,rollEmojis, authorsName, name, channel);
    }

    public void react(String emoji, MessageReactionAddEvent event)
    {

        String eventID = event.getMessageId();
        int i;
        boolean found = false;
        for (i=0;i<events.size();i++) {

            if (events.get(i).getId().equals(eventID))
            {
                found = true;
                break;
            }

        }
        if(i==events.size() && !found) {
            System.out.println("Alle Einträge durchsucht Event nicht nichfunden: "+eventID); //TODO this should be a log
            return;
        }
        ArrayList<String> rolls = events.get(i).getRolls();
        ArrayList<String> rollEmojis = events.get(i).getRollEmojis();
        int j;
        found = false;
        for (j=0;j< events.size();j++)
        {
            if(rollEmojis.get(i).equals(emoji))
            {
                found = true;
                break;
            }

        }
        if(i==events.size() && !found) {
            System.out.println("Alle Einträge durchsucht Emoji nicht nichfunden: "+emoji); //TODO this should be a log
            return;
        }

        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRolesByName(rolls.get(j),true).get(0)).queue();
    }



    public class WrongNumberOfRollsException extends Exception
    {
        public String value;
        WrongNumberOfRollsException(String value)
        {
            this.value = value;
        }
    }
}


