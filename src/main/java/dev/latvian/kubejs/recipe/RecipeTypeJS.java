package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.registry.Registry;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class RecipeTypeJS {
	public final RecipeSerializer serializer;
	public final Supplier<RecipeJS> factory;
	private final String string;
	
	public RecipeTypeJS(RecipeSerializer s, @ID String id, Supplier<RecipeJS> f) {
		serializer = s;
		factory = f;
		string = UtilsJS.getID(id);
	}
	
	public RecipeTypeJS(@ID String id, Supplier<RecipeJS> f) {
		this(Objects.requireNonNull(Registry.RECIPE_SERIALIZER.get(UtilsJS.getMCID(id))), id, f);
	}
	
	public RecipeTypeJS(RecipeSerializer s, Supplier<RecipeJS> f) {
		this(s, Objects.requireNonNull(Registry.RECIPE_SERIALIZER.getId(s)).toString(), f);
	}
	
	public boolean isCustom() {
		return false;
	}
	
	@Override
	public String toString() {
		return string;
	}
	
	@Override
	public int hashCode() {
		return string.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return string.equals(obj.toString());
	}
}