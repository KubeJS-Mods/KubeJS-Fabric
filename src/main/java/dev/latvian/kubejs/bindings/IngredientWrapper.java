package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.*;
import dev.latvian.kubejs.util.UtilsJS;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class IngredientWrapper {
	public IngredientJS getNone() {
		return EmptyItemStackJS.INSTANCE;
	}
	
	public IngredientJS getAll() {
		return MatchAllIngredientJS.INSTANCE;
	}
	
	public IngredientJS of(Object object) {
		return IngredientJS.of(object);
	}
	
	public IngredientJS custom(Predicate<ItemStackJS> predicate) {
		return predicate::test;
	}
	
	public IngredientJS matchAny(Object[] objects) {
		MatchAnyIngredientJS ingredient = new MatchAnyIngredientJS();
		ingredient.addAll(objects);
		return ingredient;
	}
	
	public IngredientJS tag(@ID String tag) {
		return new TagIngredientJS(UtilsJS.getMCID(tag), 1);
	}
	
	public IngredientJS tag(@ID String tag, int count) {
		return new TagIngredientJS(UtilsJS.getMCID(tag), count);
	}
	
	public IngredientJS mod(String modId) {
		return new ModIngredientJS(modId);
	}
	
	public IngredientJS regex(Pattern pattern) {
		return new RegexIngredientJS(pattern);
	}
	
	public IngredientJS regex(String pattern) {
		return regex(Pattern.compile(pattern));
	}
}