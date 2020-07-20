package dev.latvian.kubejs.core;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.recipe.RecipeEventJS;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import java.util.Map;

/**
 * @author LatvianModder
 */
public interface RecipeManagerKJS
{
	void setRecipesKJS(Map<RecipeType<?>, Map<Identifier, Recipe<?>>> map);

	default void customRecipesKJS(Map<Identifier, JsonObject> jsonMap)
	{
		if (RecipeEventJS.instance != null)
		{
			RecipeEventJS.instance.post((RecipeManager) this, jsonMap);
			RecipeEventJS.instance = null;
		}
	}
}