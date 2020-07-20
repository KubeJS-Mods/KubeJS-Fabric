package dev.latvian.kubejs.script;

import dev.latvian.kubejs.util.WithAttachedData;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class AttachDataEvent<T extends WithAttachedData>
{
	public static final Event<Consumer<AttachDataEvent>> EVENT = EventFactory.createArrayBacked(Consumer.class, consumers -> event -> {
		for (Consumer<AttachDataEvent> consumer : consumers)
		{
			consumer.accept(event);
		}
	});
	private final DataType<T> type;
	private final T parent;

	public AttachDataEvent(DataType<T> t, T p)
	{
		type = t;
		parent = p;
	}

	public DataType<T> getType()
	{
		return type;
	}

	public T getParent()
	{
		return parent;
	}

	public void add(String id, Object object)
	{
		parent.getData().put(id, object);
	}
}