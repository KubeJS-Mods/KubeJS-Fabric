package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.callback.item.ItemEntityTossCallback;
import dev.latvian.kubejs.callback.item.ItemRightClickEntityCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
	@Inject(at = @At("RETURN"), method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;")
	void onToss(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> callbackInformationReturnable) {
		if (!((Object) this instanceof Player) || callbackInformationReturnable.getReturnValue() == null) return;
		
		ItemEntityTossCallback.EVENT.invoker().toss((Player) (Object) this, callbackInformationReturnable.getReturnValue());
	}
	
	@Inject(at = @At("RETURN"), method = "interact")
	void onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		ItemRightClickEntityCallback.EVENT.invoker().interact(player, (Entity) (Object) this, hand, player.blockPosition());
	}
}
