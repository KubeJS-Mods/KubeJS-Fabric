package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.callback.item.ItemEntityTossCallback;
import dev.latvian.kubejs.callback.item.ItemRightClickEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public class EntityMixin {
	@Inject(at = @At("RETURN"), method = "dropStack(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;")
	void onToss(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> callbackInformationReturnable) {
		if (!((Object) this instanceof PlayerEntity) || callbackInformationReturnable.getReturnValue() == null) return;

		ItemEntityTossCallback.EVENT.invoker().toss((PlayerEntity) (Object) this, callbackInformationReturnable.getReturnValue());
	}

	@Inject(at = @At("RETURN"), method = "interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;")
	void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		ItemRightClickEntityCallback.EVENT.invoker().interact(player, (Entity) (Object) this, hand, player.getBlockPos());
	}
}
