package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.TagCollectionKJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
@Mixin(TagLoader.class)
public abstract class TagCollectionMixin<T> implements TagCollectionKJS<T> {
	@Inject(method = "load", at = @At("HEAD"))
	private void customTags(Map<ResourceLocation, Tag.Builder> tags, CallbackInfoReturnable<TagCollection<T>> cir) {
		customTagsKJS(tags);
	}
	
	@Override
	@Accessor("idToValue")
	public abstract Function<ResourceLocation, Optional<T>> getRegistryGetterKJS();
	
	@Override
	@Accessor("directory")
	public abstract String getResourceLocationPrefixKJS();
	
	@Override
	@Accessor("name")
	public abstract String getItemTypeNameKJS();
}