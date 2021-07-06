package services.reactions;

import dataObjects.ReactionRoleEvent;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class RoleService {

    ArrayList<ReactionRoleEvent> events = new ArrayList<>();
    private static final RoleService instance = new RoleService();
    public static RoleService getInstance() {
        return instance;
    }

    public void roll( @NotNull MessageChannel channel)
    {
        channel.sendMessage("https://www.youtube.com/watch?v=2ocykBzWDiM&feature=emb_title").queue();
        //well yes it does what's intended to do.
    }

    public void startPersonalReactionRoleEvent(@NotNull LinkedHashMap<String, String> roleToEmoji, String name, MessageChannel channel) throws WrongNumberOfRolesException {

        if(roleToEmoji.size() < 1) {
            throw new WrongNumberOfRolesException("roleToEmoji.size: " + roleToEmoji.size() + " was unexpected");
        }

        ReactionRoleEvent event = new ReactionRoleEvent(roleToEmoji, name);
        events.add(event);
        event.printEvent(channel);
    }

    public void startNumberedReactionRoleEvent(@NotNull ArrayList<String> roles, String name, MessageChannel channel) throws WrongNumberOfRolesException {
        if(roles.size() > 10 || roles.size() < 1) {
            throw new WrongNumberOfRolesException("roles.size: " + roles.size() + " was unexpected");
        }

        //max of 8, then grow since 6 elements. Statistically, 8 is more likely to get chosen ;)
        LinkedHashMap<String, String> roleToEmoji = new LinkedHashMap<>(8, 0.75f);

        switch (roles.size()) {
            case (10):
                roleToEmoji.put(roles.get(9), "\uD83D\uDD1F");
            case (9):
                roleToEmoji.put(roles.get(8), "9⃣");
            case(8):
                roleToEmoji.put(roles.get(7), "8⃣");
            case(7):
                roleToEmoji.put(roles.get(6), "7⃣");
            case(6):
                roleToEmoji.put(roles.get(5), "6⃣");
            case(5):
                roleToEmoji.put(roles.get(4), "5⃣");
            case(4):
                roleToEmoji.put(roles.get(3), "4⃣");
            case(3):
                roleToEmoji.put(roles.get(2), "3⃣");
            case(2):
                roleToEmoji.put(roles.get(1), "2️⃣");
            case(1):
                roleToEmoji.put(roles.get(0), "1️⃣");
        } //well yes that's right because of ne breaks;

        roleToEmoji = reverseMap(roleToEmoji);

        startPersonalReactionRoleEvent(roleToEmoji, name, channel);
    }

    public void getRole(String emoji, MessageReactionAddEvent event) throws MassageNotFoundException {
        String role = getMatchingRole(emoji, event.getMessageId());

        //the role must be available!
        event.getGuild().addRoleToMember(event.getUserId(), event.getGuild().getRolesByName(role,true).get(0)).queue();
    }

    public void loseRole(String emoji, MessageReactionRemoveEvent event) throws MassageNotFoundException {
        String role = getMatchingRole(emoji, event.getMessageId());
        event.getGuild().removeRoleFromMember(event.getUserId(), event.getGuild().getRolesByName(role,true).get(0)).queue();
    }

    private String getMatchingRole(String emoji, String messageId) throws MassageNotFoundException {

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

    public static class WrongNumberOfRolesException extends Exception
    {
        public String value;
        WrongNumberOfRolesException(String value)
        {
            this.value = value;
        }
    }

    public static class MassageNotFoundException extends Exception
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