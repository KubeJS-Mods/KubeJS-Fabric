package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.TagCollectionKJS;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
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
@Mixin(TagGroupLoader.class)
public abstract class TagCollectionMixin<T> implements TagCollectionKJS<T> {
	@Inject(method = "applyReload", at = @At("HEAD"))
	private void customTags(Map<Identifier, Tag.Builder> tags, CallbackInfoReturnable<TagGroup<T>> cir) {
		customTagsKJS(tags);
	}
	
	@Override
	@Accessor("registryGetter")
	public abstract Function<Identifier, Optional<T>> getRegistryGetterKJS();
	
	@Override
	@Accessor("dataType")
	public abstract String getResourceLocationPrefixKJS();
	
	@Override
	@Accessor("entryType")
	public abstract String getItemTypeNameKJS();
}