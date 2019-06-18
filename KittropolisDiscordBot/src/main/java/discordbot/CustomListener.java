package discordbot;

import net.dv8tion.jda.core.events.Event;

public interface CustomListener {
	public abstract void call(Event event);
}
