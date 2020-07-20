package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.callback.item.ItemDestroyCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
	        method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V")
	<T extends LivingEntity> void onBreak(int amount, T entity, Consumer<T> breakCallback, CallbackInfo ci) {
		if (!(entity instanceof PlayerEntity)) return;
		ItemDestroyCallback.EVENT.invoker().destroy((PlayerEntity) entity, this.copy(), entity.getActiveHand());
	}
}
