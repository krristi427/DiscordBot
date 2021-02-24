package dataObjects;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;

public class ReactionRollEvent {
    private ArrayList<String> rolls;
    private ArrayList<String> rollEmojis;
    private String eventName;
    private String id;

    public ReactionRollEvent(ArrayList<String> rolls, ArrayList<String> rollEmojis, String eventName) {
        this.rolls = rolls;
        this.eventName = eventName;
        this.rollEmojis = rollEmojis;

    }

    public ArrayList<String> getRolls() {
        return rolls;
    }

    public ArrayList<String> getRollEmojis() {
        return rollEmojis;
    }

    public void setRolls(ArrayList<String> rolls) {
        this.rolls = rolls;
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

        info.setTitle("Wähle deine Rolle für"+eventName+":");
        info.addField(new MessageEmbed.Field("","Reagiere mit einem Emoji um der entsprechenden Rolle zugewiesen zu werden",false));
        String content = "";
        for (int i=0; i<rolls.size(); i++)
            content+="Drücke "+rollEmojis.get(i)+" um der Rolle "+rolls.get(i)+" zugewiesen zu werden.\n";
        info.addField(new MessageEmbed.Field("",content,false));
        channel.sendMessage(info.build()).queue((message) -> {
            for (int i=0; i<rolls.size(); i++)
                message.addReaction(rollEmojis.get(i)).queue();
            info.setFooter("ID: "+id);
        });



    }
    public void makeEvent(MessageChannel channel){
        long messageId = channel.getLatestMessageIdLong();
        for (int i=rolls.size()-1; i>=0; i--) {

            channel.addReactionById(messageId, "\uD83D\uDCA6").queue();

            }
    }
}
