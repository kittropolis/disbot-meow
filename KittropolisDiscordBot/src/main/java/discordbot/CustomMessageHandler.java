package discordbot;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface CustomMessageHandler {
	public void call(MessageReceivedEvent event);
}
