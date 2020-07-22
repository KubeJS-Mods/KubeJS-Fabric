package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.callback.item.EmptyLeftClickAirCallback;
import dev.latvian.kubejs.callback.item.EmptyRightClickAirCallback;
import dev.latvian.kubejs.client.ClientProperties;
import dev.latvian.kubejs.client.KubeJSClientEventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	@Nullable
	public ClientPlayerEntity player;
	
	@Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
	private void getWindowTitle(CallbackInfoReturnable<String> ci) {
		String s = ClientProperties.get().title;
		
		if (!s.isEmpty()) {
			ci.setReturnValue(s);
		}
	}
	
	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",
	        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;reset()V"))
	private void disconnect(Screen screen, CallbackInfo ci) {
		KubeJSClientEventHandler.ON_LOGOUT.invoker().run();
	}

	// TODO: Perhaps make this also happen on the server.
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Hand;values()[Lnet/minecraft/util/Hand;"),
	        method = "doItemUse()V")
	void onRightClickUse(CallbackInfo ci) {
		ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
		if (stack.isEmpty()) {
			EmptyRightClickAirCallback.EVENT.invoker().rightClickEmpty(MinecraftClient.getInstance().player, Hand.MAIN_HAND, MinecraftClient.getInstance().player.getBlockPos());
		}
	}

	// TODO: Perhaps make this also happen on the server.
	@Inject(locals = LocalCapture.CAPTURE_FAILHARD,
	        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/BlockHitResult;getBlockPos()Lnet/minecraft/util/math/BlockPos;"),
	        method = "doAttack()V")
	void onLeftClickUse(CallbackInfo ci) {
		EmptyLeftClickAirCallback.EVENT.invoker().leftClickEmpty(MinecraftClient.getInstance().player, Hand.MAIN_HAND, MinecraftClient.getInstance().player.getBlockPos());
	}
}
