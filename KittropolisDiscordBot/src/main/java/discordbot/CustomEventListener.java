package discordbot;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

public abstract class CustomEventListener implements EventListener {

	public abstract void onEvent(Event event);

}
