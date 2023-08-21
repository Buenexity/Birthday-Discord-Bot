package events;

import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Hello_Event extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event)
    {
        String message_sent = event.getMessage().getContentRaw();
        if(message_sent.equalsIgnoreCase("/birthday"))
        {
            event.getChannel().sendMessage("This is a bot to register birthdays").queue();
            event.getChannel().sendMessage("To register: !register DD MM YYYY").queue();
        }

    }

}
//MTEzMzgxNjY1NjUyMzY5NDA4MA.G_kUa3.2EjFwXzXgDZzB943RitoSuOEYfmIEIhMqSs9L8