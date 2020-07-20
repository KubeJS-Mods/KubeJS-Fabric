package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.client.ClientProperties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;

/**
 * @author LatvianModder
 */
@Environment(EnvType.CLIENT)
@Mixin(Window.class)
public class MainWindowMixin {
	@Inject(method = "setIcon", at = @At("HEAD"), cancellable = true)
	private void setWindowIcon(InputStream icon16, InputStream icon32, CallbackInfo ci) {
		if (ClientProperties.get().cancelIconUpdate()) {
			ci.cancel();
		}
	}
}