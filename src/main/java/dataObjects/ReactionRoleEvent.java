package dataObjects;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReactionRoleEvent {

    @Getter
    @Setter
    private String eventName;

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private LinkedHashMap<String, String> roleToEmoji;

    public ReactionRoleEvent(LinkedHashMap<String, String> roleToEmoji, String eventName) {

        this.roleToEmoji = roleToEmoji;
        this.eventName = eventName;
    }

    public void printEvent(MessageChannel channel){
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(0xdb00ff);

        info.setTitle("Choose a Role for " + eventName + ":");
        info.setFooter("React with an emoji to this message, in order to get the corresponding role");
        StringBuilder content = new StringBuilder();

        for (Map.Entry<String, String> pair: roleToEmoji.entrySet()) {

            String role = pair.getKey();
            String emoji = pair.getValue();

            content.append("React with: ").append(emoji).append(" in order to get the role: ").append(role).append("\n");
        }

        info.setDescription(content.toString());

        channel.sendMessage(info.build()).queue((message) -> {

            for (Map.Entry<String, String> pair: roleToEmoji.entrySet()) {

                String emoji = pair.getValue();
                message.addReaction(emoji).queue();
            }
            id = message.getId();
        });
    }

    public void makeEvent(MessageChannel channel){
        long messageId = channel.getLatestMessageIdLong();
        for (int i = roleToEmoji.size() - 1; i >= 0; i--) {

            channel.addReactionById(messageId, "\uD83D\uDCA6").queue();

        }
    }
}
