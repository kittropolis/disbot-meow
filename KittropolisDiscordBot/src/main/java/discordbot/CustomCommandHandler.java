package discordbot;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface CustomCommandHandler {
	public void call(MessageReceivedEvent event, String[] params);
}
