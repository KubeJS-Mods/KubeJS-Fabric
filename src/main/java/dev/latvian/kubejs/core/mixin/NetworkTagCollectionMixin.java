package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.NetworkTagCollectionKJS;
import net.minecraft.tag.RegistryTagContainer;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(RegistryTagContainer.class)
public abstract class NetworkTagCollectionMixin<T> implements NetworkTagCollectionKJS
{
	@Override
	@Accessor("registry")
	public abstract <T> Registry<T> getRegistryKJS();
}