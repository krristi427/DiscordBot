package services;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

public interface Subject {

    void registerObserver(Observer observer);
    void removeObserver(Observer observer);

    //overloading the method, as different observers look for different Events
    void notifyObservers(MessageReceivedEvent event);
    void notifyObservers(GuildMessageReceivedEvent event);
    void notifyObservers(MessageReactionAddEvent event);
    void notifyObservers(MessageReactionRemoveEvent event);

}
