package dev.latvian.kubejs.recipe.minecraft;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

/**
 * @author LatvianModder
 */
public class StonecuttingRecipeJS extends RecipeJS {
	@Override
	public void create(ListJS args) {
		ItemStackJS result = ItemStackJS.of(args.get(0));
		outputItems.add(result);
		IngredientJS ingredient = IngredientJS.of(args.get(1));
		inputItems.add(ingredient);
	}
	
	@Override
	public void deserialize() {
		ItemStackJS result = ItemStackJS.resultFromRecipeJson(json.get("result"));
		outputItems.add(result);
		
		IngredientJS ingredient = IngredientJS.ingredientFromRecipeJson(json.get("ingredient"));
		inputItems.add(ingredient);
	}
	
	@Override
	public void serialize() {
		json.add("ingredient", inputItems.get(0).toJson());
		json.addProperty("result", outputItems.get(0).getId());
		json.addProperty("count", outputItems.get(0).getCount());
	}
}