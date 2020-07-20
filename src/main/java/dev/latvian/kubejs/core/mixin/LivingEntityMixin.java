package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.callback.item.ItemEntityPickupCallback;
import dev.latvian.kubejs.core.LivingEntityKJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author LatvianModder
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityKJS
{
	@Inject(method = "eatFood", at = @At("HEAD"))
	private void foodEaten(World world, ItemStack item, CallbackInfoReturnable<ItemStack> ci)
	{
		foodEatenKJS(item);
	}

	@Inject(at = @At("HEAD"), method = "sendPickup(Lnet/minecraft/entity/Entity;I)V")
	void onPickupStack(Entity entity, int count, CallbackInfo callbackInformation) {
		if (!((Object) this instanceof PlayerEntity) || !(entity instanceof ItemEntity)) return;

		ItemEntityPickupCallback.EVENT.invoker().pickup((PlayerEntity) (Object) this, (ItemEntity) entity);
	}
}