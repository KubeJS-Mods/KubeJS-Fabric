package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * @author LatvianModder
 */
public class SoundRegistryEventJS extends EventJS {
	public final Registry<SoundEvent> registry;
	
	public SoundRegistryEventJS(Registry<SoundEvent> r) {
		registry = r;
	}
	
	public void register(@ID String id) {
		ResourceLocation r = UtilsJS.getMCID(KubeJS.appendModId(id));
		Registry.register(registry, r, new SoundEvent(r));
	}
}