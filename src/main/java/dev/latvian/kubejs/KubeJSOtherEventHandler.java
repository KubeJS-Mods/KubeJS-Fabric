package dev.latvian.kubejs;

import dev.latvian.kubejs.client.SoundRegistryEventJS;
import dev.latvian.kubejs.core.AfterScriptLoadCallback;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.util.registry.Registry;

/**
 * @author LatvianModder
 */
public class KubeJSOtherEventHandler
{
	public void init()
	{
		AfterScriptLoadCallback.EVENT.register(this::registry);
	}

	private void registry()
	{
		new SoundRegistryEventJS(Registry.SOUND_EVENT).post(ScriptType.STARTUP, KubeJSEvents.SOUND_REGISTRY);
	}
}