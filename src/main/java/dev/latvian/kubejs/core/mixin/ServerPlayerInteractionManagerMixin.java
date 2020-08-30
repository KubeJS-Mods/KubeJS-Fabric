package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.callback.item.ItemRightClickAirCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerInteractionManagerMixin {
	@Shadow public ServerPlayer player;
	
	@Inject(at = @At("HEAD"), cancellable = true,
	        method = "useItem")
	void onUseItem(ServerPlayer player, Level world, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callbackInformationReturnable) {
		ItemRightClickAirCallback.EVENT.invoker().rightClick(player, stack, hand, player.blockPosition());
	}
}
