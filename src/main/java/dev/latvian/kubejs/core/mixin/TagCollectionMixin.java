package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.TagCollectionKJS;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * @author LatvianModder
 */
@Mixin(TagContainer.class)
public abstract class TagCollectionMixin<T> implements TagCollectionKJS
{
	@Inject(method = "applyReload", at = @At("HEAD"))
	private void customTags(Map<Identifier, Tag.Builder> map, CallbackInfo ci)
	{
		customTagsKJS(map);
	}

	@Override
	@Accessor("dataType")
	public abstract String getResourceLocationPrefixKJS();

	@Override
	@Accessor("entryType")
	public abstract String getItemTypeNameKJS();
}