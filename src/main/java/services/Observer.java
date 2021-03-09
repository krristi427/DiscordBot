package services;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

public interface Observer {

    void update(MessageReceivedEvent event);
    void update(GuildMessageReceivedEvent event);
    void update(MessageReactionAddEvent event);
    void update(MessageReactionRemoveEvent event);
}
