package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.StonecuttingRecipeJS;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * @author LatvianModder
 */
public class KubeJSRecipeEventHandler implements KubeJSInitializer {
	@Override
	public void onKubeJSInitialization() {
		RegisterRecipeHandlersEvent.EVENT.register(event -> {
			event.register(new RecipeTypeJS(RecipeSerializer.SHAPED_RECIPE, ShapedRecipeJS::new));
			event.register(new RecipeTypeJS(RecipeSerializer.SHAPELESS_RECIPE, ShapelessRecipeJS::new));
			event.register(new RecipeTypeJS(RecipeSerializer.STONECUTTER, StonecuttingRecipeJS::new));
			
			for (CookingRecipeJS.Type type : CookingRecipeJS.Type.values()) {
				event.register(new RecipeTypeJS(type.serializer, () -> new CookingRecipeJS(type)));
			}
		});
	}
}