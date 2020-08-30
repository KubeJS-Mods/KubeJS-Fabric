package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.callback.item.ItemEntityPickupCallback;
import dev.latvian.kubejs.core.LivingEntityKJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author LatvianModder
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityKJS {
	@Inject(method = "eat", at = @At("HEAD"))
	private void foodEaten(Level world, ItemStack item, CallbackInfoReturnable<ItemStack> ci) {
		foodEatenKJS(item);
	}
	
	@Inject(at = @At("HEAD"), method = "take")
	void onPickupStack(Entity entity, int count, CallbackInfo callbackInformation) {
		if (!((Object) this instanceof Player) || !(entity instanceof ItemEntity)) return;
		
		ItemEntityPickupCallback.EVENT.invoker().pickup((Player) (Object) this, (ItemEntity) entity);
	}
}