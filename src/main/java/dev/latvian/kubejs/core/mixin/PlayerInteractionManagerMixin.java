package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.PlayerInteractionManagerKJS;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(ServerPlayerGameMode.class)
public abstract class PlayerInteractionManagerMixin implements PlayerInteractionManagerKJS {
	@Override
	@Accessor("isDestroyingBlock")
	public abstract boolean isDestroyingBlockKJS();
}