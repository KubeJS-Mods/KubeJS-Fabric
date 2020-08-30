package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.callback.item.EmptyLeftClickAirCallback;
import dev.latvian.kubejs.callback.item.EmptyRightClickAirCallback;
import dev.latvian.kubejs.client.ClientProperties;
import dev.latvian.kubejs.client.KubeJSClientEventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftClientMixin {
	@Shadow
	@Nullable
	public LocalPlayer player;
	
	@Inject(method = "createTitle", at = @At("HEAD"), cancellable = true)
	private void getWindowTitle(CallbackInfoReturnable<String> ci) {
		String s = ClientProperties.get().title;
		
		if (!s.isEmpty()) {
			ci.setReturnValue(s);
		}
	}
	
	@Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V",
	        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;resetData()V"))
	private void clearLevel(Screen screen, CallbackInfo ci) {
		KubeJSClientEventHandler.ON_LOGOUT.invoker().run();
	}

	// TODO: Perhaps make this also happen on the server.
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionHand;values()[Lnet/minecraft/world/InteractionHand;"),
	        method = "startUseItem()V")
	void onRightClickUse(CallbackInfo ci) {
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (stack.isEmpty()) {
			EmptyRightClickAirCallback.EVENT.invoker().rightClickEmpty(Minecraft.getInstance().player, InteractionHand.MAIN_HAND, Minecraft.getInstance().player.blockPosition());
		}
	}

	// TODO: Perhaps make this also happen on the server.
	@Inject(locals = LocalCapture.CAPTURE_FAILHARD,
	        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"),
	        method = "startAttack")
	void onLeftClickUse(CallbackInfo ci) {
		EmptyLeftClickAirCallback.EVENT.invoker().leftClickEmpty(Minecraft.getInstance().player, InteractionHand.MAIN_HAND, Minecraft.getInstance().player.blockPosition());
	}
}
