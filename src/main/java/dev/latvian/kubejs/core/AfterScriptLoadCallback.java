package dev.latvian.kubejs.core;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface AfterScriptLoadCallback
{
	Event<AfterScriptLoadCallback> EVENT = EventFactory.createArrayBacked(AfterScriptLoadCallback.class, callbacks -> () -> {
		for (AfterScriptLoadCallback callback : callbacks)
		{
			callback.afterLoad();
		}
	});

	void afterLoad();
}
