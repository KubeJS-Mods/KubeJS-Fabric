package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.recipe.minecraft.CookingRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.ShapedRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.ShapelessRecipeJS;
import dev.latvian.kubejs.recipe.minecraft.StonecuttingRecipeJS;
import net.minecraft.recipe.RecipeSerializer;

/**
 * @author LatvianModder
 */
public class KubeJSRecipeEventHandler implements KubeJSInitializer {
	@Override
	public void onKubeJSInitialization() {
		RegisterRecipeHandlersEvent.EVENT.register(event -> {
			event.register(new RecipeTypeJS(RecipeSerializer.SHAPED, ShapedRecipeJS::new));
			event.register(new RecipeTypeJS(RecipeSerializer.SHAPELESS, ShapelessRecipeJS::new));
			event.register(new RecipeTypeJS(RecipeSerializer.STONECUTTING, StonecuttingRecipeJS::new));
			
			for (CookingRecipeJS.Type type : CookingRecipeJS.Type.values()) {
				event.register(new RecipeTypeJS(type.serializer, () -> new CookingRecipeJS(type)));
			}
		});
	}
}