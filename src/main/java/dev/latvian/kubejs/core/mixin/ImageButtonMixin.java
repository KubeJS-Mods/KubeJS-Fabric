package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.ImageButtonKJS;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author LatvianModder
 */
@Mixin(TexturedButtonWidget.class)
public abstract class ImageButtonMixin implements ImageButtonKJS
{
	@Override
	@Accessor("texture")
	public abstract Identifier getButtonTextureKJS();
}