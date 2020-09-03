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
			event.register("minecraft:crafting_shaped", ShapedRecipeJS::new);
			event.register("minecraft:crafting_shapeless", ShapelessRecipeJS::new);
			event.register("minecraft:stonecutting", StonecuttingRecipeJS::new);
			event.register("minecraft:smelting", CookingRecipeJS::new);
			event.register("minecraft:blasting", CookingRecipeJS::new);
			event.register("minecraft:smoking", CookingRecipeJS::new);
			event.register("minecraft:campfire_cooking", CookingRecipeJS::new);
		});
	}
}