import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

public abstract class Subject {

    abstract void registerObserver(Observer observer);
    abstract void removeObserver(Observer observer);

    //overloading the method, as different observers look for different Events
    abstract void notifyObservers(MessageReceivedEvent event);
    abstract void notifyObservers(GuildMessageReceivedEvent event);
    abstract void notifyObservers(MessageReactionAddEvent event);
    abstract void notifyObservers(MessageReactionRemoveEvent event);

}
