package dev.latvian.kubejs.world;

import dev.latvian.kubejs.script.AttachDataEvent;
import dev.latvian.kubejs.script.DataType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class AttachWorldDataEvent extends AttachDataEvent<WorldJS> {
	public static final Event<Consumer<AttachWorldDataEvent>> EVENT = EventFactory.createArrayBacked(Consumer.class, consumers -> event -> {
		for (Consumer<AttachWorldDataEvent> consumer : consumers) {
			consumer.accept(event);
		}
	});
	
	public AttachWorldDataEvent(WorldJS w) {
		super(DataType.WORLD, w);
	}
}