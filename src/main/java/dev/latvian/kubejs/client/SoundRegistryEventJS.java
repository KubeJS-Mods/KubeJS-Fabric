package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author LatvianModder
 */
public class SoundRegistryEventJS extends EventJS
{
	public final Registry<SoundEvent> registry;

	public SoundRegistryEventJS(Registry<SoundEvent> r)
	{
		registry = r;
	}

	public void register(@ID String id)
	{
		Identifier r = UtilsJS.getMCID(KubeJS.appendModId(id));
		Registry.register(registry, r, new SoundEvent(r));
	}
}