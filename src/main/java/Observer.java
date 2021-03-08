import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

public abstract class Observer {

    //overloading the method, as different observers look for different Events
    abstract void update(MessageReceivedEvent event);
    abstract void update(GuildMessageReceivedEvent event);
    abstract void update(MessageReactionAddEvent event);
    abstract void update(MessageReactionRemoveEvent event);
}
