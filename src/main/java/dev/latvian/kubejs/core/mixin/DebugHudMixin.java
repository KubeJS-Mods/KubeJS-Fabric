package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.client.KubeJSClientEventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
@Environment(EnvType.CLIENT)
public class DebugHudMixin {
	@Inject(at = @At("RETURN"), method = "getLeftText")
	protected void getLeftTextKJS(CallbackInfoReturnable<List<String>> info) {
		KubeJSClientEventHandler.ON_DEBUG_TEXT.invoker().accept(true, info.getReturnValue());
	}
	
	@Inject(at = @At("RETURN"), method = "getRightText")
	protected void getRightTextKJS(CallbackInfoReturnable<List<String>> info) {
		KubeJSClientEventHandler.ON_DEBUG_TEXT.invoker().accept(false, info.getReturnValue());
	}
}
