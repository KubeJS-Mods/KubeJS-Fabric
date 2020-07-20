package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.ResourceLoadProgressGuiKJS;
import net.minecraft.client.gui.screen.SplashScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * @author LatvianModder
 */
@Mixin(SplashScreen.class)
public abstract class ResourceLoadProgressGuiMixin implements ResourceLoadProgressGuiKJS
{
	@ModifyArg(method = "render", remap = false, at = @At(value = "INVOKE",
	                                                      target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"),
	           index = 5)
	private int backgroundColorKJS(int color)
	{
		return getBackgroundColorKJS(color);
	}

	@ModifyArg(method = "renderProgressBar", remap = false, at = @At(value = "INVOKE", ordinal = 4,
	                                                                 target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"),
	           index = 5)
	private int barBorderColorKJS(int color)
	{
		return getBarColorKJS(color);
	}

	@ModifyArg(method = "renderProgressBar", remap = false, at = @At(value = "INVOKE", ordinal = 0,
	                                                                 target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"),
	           index = 5)
	private int barColorKJS1(int color)
	{
		return getBarBorderColorKJS(color);
	}

	@ModifyArg(method = "renderProgressBar", remap = false, at = @At(value = "INVOKE", ordinal = 1,
	                                                                 target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"),
	           index = 5)
	private int barColorKJS2(int color)
	{
		return getBarBorderColorKJS(color);
	}

	@ModifyArg(method = "renderProgressBar", remap = false, at = @At(value = "INVOKE", ordinal = 2,
	                                                                 target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"),
	           index = 5)
	private int barColorKJS3(int color)
	{
		return getBarBorderColorKJS(color);
	}

	@ModifyArg(method = "renderProgressBar", remap = false, at = @At(value = "INVOKE", ordinal = 3,
	                                                                 target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"),
	           index = 5)
	private int barColorKJS4(int color)
	{
		return getBarBorderColorKJS(color);
	}
}