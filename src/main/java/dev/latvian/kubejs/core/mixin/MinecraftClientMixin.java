package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.callback.item.EmptyLeftClickAirCallback;
import dev.latvian.kubejs.callback.item.EmptyRightClickAirCallback;
import dev.latvian.kubejs.item.ItemRightClickEmptyEventJS;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	@Nullable
	public ClientPlayerEntity player;

	@Inject(locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"), method = "Lnet/minecraft/client/MinecraftClient;doItemUse()V")
	void onUse(CallbackInfo ci, Hand[] var1, int var2, int var3) {
		ItemStack stack = player.getStackInHand(var1[var3]);
		if (stack.isEmpty()) {
			EmptyRightClickAirCallback.EVENT.invoker().rightClickEmpty(MinecraftClient.getInstance().player, var1[var3], MinecraftClient.getInstance().player.getBlockPos());
		}
	}

	@Inject(locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/BlockHitResult;getBlockPos()Lnet/minecraft/util/math/BlockPos;"), method = "Lnet/minecraft/client/MinecraftClient;doAttack()V")
	void onUse(CallbackInfo ci) {
		EmptyLeftClickAirCallback.EVENT.invoker().leftClickEmpty(MinecraftClient.getInstance().player, Hand.MAIN_HAND, MinecraftClient.getInstance().player.getBlockPos());
	}
}
