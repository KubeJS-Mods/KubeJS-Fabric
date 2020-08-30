package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.callback.item.ItemDestroyCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow
	public abstract ItemStack copy();
	
	@Inject(at = @At(value = "RETURN", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"),
	        method = "hurtAndBreak")
	<T extends LivingEntity> void onBreak(int amount, T entity, Consumer<T> breakCallback, CallbackInfo ci) {
		if (!(entity instanceof Player)) return;
		ItemDestroyCallback.EVENT.invoker().destroy((Player) entity, this.copy(), entity.getUsedItemHand());
	}
}
