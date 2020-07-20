package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.client.ClientProperties;
import dev.latvian.kubejs.client.KubeJSClientEventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author LatvianModder
 */
@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public abstract class MinecraftMixin
{
	@Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
	private void getWindowTitle(CallbackInfoReturnable<String> ci)
	{
		String s = ClientProperties.get().title;

		if (!s.isEmpty())
		{
			ci.setReturnValue(s);
		}
	}

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",
	        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;reset()V"))
	private void disconnect(Screen screen, CallbackInfo ci)
	{
		KubeJSClientEventHandler.ON_LOGOUT.invoker().run();
	}
}