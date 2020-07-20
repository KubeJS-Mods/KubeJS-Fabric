package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.PlayerInteractionManagerKJS;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(ServerPlayerInteractionManager.class)
public abstract class PlayerInteractionManagerMixin implements PlayerInteractionManagerKJS {
	@Override
	@Accessor("mining")
	public abstract boolean isDestroyingBlockKJS();
}