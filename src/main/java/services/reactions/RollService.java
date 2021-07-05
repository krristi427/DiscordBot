package services.reactions;

import dataObjects.ReactionRollEvent;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public void startPersonalReactionRollEvent(@NotNull LinkedHashMap<String, String> roleToEmoji, String name, MessageChannel channel) throws WrongNumberOfRollsException {

        if(roleToEmoji.size() < 1) {
            throw new WrongNumberOfRollsException("roleToEmoji.size: " + roleToEmoji.size() + " was unexpected");
        }

        ReactionRollEvent event = new ReactionRollEvent(roleToEmoji, name);
        events.add(event);
        event.printEvent(channel);
    }

    public void startNumberedReactionRollEvent( @NotNull ArrayList<String> rolls, String name, MessageChannel channel) throws WrongNumberOfRollsException {
        if(rolls.size() > 10 || rolls.size() < 1) {
            throw new WrongNumberOfRollsException("rolls.size: "+rolls.size()+" was unexpected");
        }

        //max of 8, then grow since 6 elements. Statistically, 8 is more likely to get chosen ;)
        LinkedHashMap<String, String> roleToEmoji = new LinkedHashMap<>(8, 0.75f);

        switch (rolls.size()) {
            case (10):
                roleToEmoji.put(rolls.get(9), "\uD83D\uDD1F");
            case (9):
                roleToEmoji.put(rolls.get(8), "9⃣");
            case(8):
                roleToEmoji.put(rolls.get(7), "8⃣");
            case(7):
                roleToEmoji.put(rolls.get(6), "7⃣");
            case(6):
                roleToEmoji.put(rolls.get(5), "6⃣");
            case(5):
                roleToEmoji.put(rolls.get(4), "5⃣");
            case(4):
                roleToEmoji.put(rolls.get(3), "4⃣");
            case(3):
                roleToEmoji.put(rolls.get(2), "3⃣");
            case(2):
                roleToEmoji.put(rolls.get(1), "2️⃣");
            case(1):
                roleToEmoji.put(rolls.get(0), "1️⃣");
        } //well yes that's right because of ne breaks;

        roleToEmoji = reverseMap(roleToEmoji);

        startPersonalReactionRollEvent(roleToEmoji, name, channel);
    }

    public void getRole(String emoji, MessageReactionAddEvent event) throws MassageNotFoundException {
        String roll = getMatchingRoll(emoji, event.getMessageId());

        //the role must be available!
        event.getGuild().addRoleToMember(event.getUserId(), event.getGuild().getRolesByName(roll,true).get(0)).queue();
    }

    public void loseRole(String emoji, MessageReactionRemoveEvent event) throws MassageNotFoundException {
        String roll = getMatchingRoll(emoji, event.getMessageId());
        event.getGuild().removeRoleFromMember(event.getUserId(), event.getGuild().getRolesByName(roll,true).get(0)).queue();
    }

    private String getMatchingRoll(String emoji, String messageId) throws MassageNotFoundException {

        int i = 0;

        //get the ID and make sure it is available..it could also be null, which means it wasn't found
        Optional<Integer> eventWithID = Optional.ofNullable(findEventWithID(messageId));
        if (eventWithID.isPresent()) {
            i = eventWithID.get();
        }

        LinkedHashMap<String, String> roleToEmoji = events.get(i).getRoleToEmoji();

        Optional<String> foundKey = findKeyForValue(roleToEmoji, emoji);

        String key = "";

        if (foundKey.isPresent()) {
            key = foundKey.get();
        }

        return key;
    }

    private Integer findEventWithID(String eventID) throws MassageNotFoundException {

        for (int i = 0; i < events.size(); i++) {

            if (events.get(i).getId().equals(eventID)) {
                return i;
            }

            if(i == events.size()) {
                throw new MassageNotFoundException(eventID, "Message");
            }
        }
        return null;
    }

    private Optional<String> findKeyForValue(LinkedHashMap<String, String> roleToEmoji, String emoji) {

        return roleToEmoji.entrySet()
                .stream()
                .filter(key -> emoji.equals(
                        key.getValue()))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private LinkedHashMap<String, String> reverseMap(LinkedHashMap<String, String> roleToEmoji) {

        return roleToEmoji.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByKey(Comparator.reverseOrder())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
    }

    public class WrongNumberOfRollsException extends Exception
    {
        public String value;
        WrongNumberOfRollsException(String value)
        {
            this.value = value;
        }
    }

    public class MassageNotFoundException extends Exception
    {
        public String value;
        public String typ;
        MassageNotFoundException(String value, String typ)
        {
            this.value = value;
            this.typ = typ;
        }
    }
}

/*

return roleToEmoji.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(
                        Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));


 */


