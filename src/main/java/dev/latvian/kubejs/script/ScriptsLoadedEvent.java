package dev.latvian.kubejs.script;


import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class ScriptsLoadedEvent
{
	public static final Event<Consumer<ScriptsLoadedEvent>> EVENT = EventFactory.createArrayBacked(Consumer.class, consumers -> event -> {
		for (Consumer<ScriptsLoadedEvent> consumer : consumers)
		{
			consumer.accept(event);
		}
	});
}