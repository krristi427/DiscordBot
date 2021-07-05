package dataObjects;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReactionRollEvent {

    //TODO throw lombok at this stuff

    private String eventName;
    private String id;

    @Getter
    @Setter
    private LinkedHashMap<String, String> roleToEmoji;

    public ReactionRollEvent(LinkedHashMap<String, String> roleToEmoji, String eventName) {

        this.roleToEmoji = roleToEmoji;
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void printEvent(MessageChannel channel){
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(0xdb00ff);

        info.setTitle("Wähle deine Rolle für "+eventName+":");
        info.setFooter("Reagiere mit einem Emoji um der entsprechenden Rolle zugewiesen zu werden");
        String content = "";

        for (Map.Entry<String, String> pair: roleToEmoji.entrySet()) {

            String role = pair.getKey();
            String emoji = pair.getValue();

            content+="Drücke "+ emoji +" um der Rolle "+ role +" zugewiesen zu werden.\n";
        }

        info.setDescription(content);

        channel.sendMessage(info.build()).queue((message) -> {

            for (Map.Entry<String, String> pair: roleToEmoji.entrySet()) {

                String emoji = pair.getValue();
                message.addReaction(emoji).queue();
            }
            id = message.getId();
        });

        //TODO leerzeichen hier entfernen



    }

    public void makeEvent(MessageChannel channel){
        long messageId = channel.getLatestMessageIdLong();
        for (int i = roleToEmoji.size() - 1; i >= 0; i--) {

            channel.addReactionById(messageId, "\uD83D\uDCA6").queue();

        }
    }
}
