package dev.latvian.kubejs.core;

import dev.latvian.kubejs.server.TagEventJS;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public interface TagCollectionKJS<T> {
	default void customTagsKJS(Map<Identifier, Tag.Builder> map) {
		String c = getResourceLocationPrefixKJS().substring(5);
		String t = getItemTypeNameKJS();
		new TagEventJS<>(c, map, getRegistryGetterKJS()).post(t + ".tags");
	}
	
	Function<Identifier, Optional<T>> getRegistryGetterKJS();
	
	String getResourceLocationPrefixKJS();
	
	String getItemTypeNameKJS();
}