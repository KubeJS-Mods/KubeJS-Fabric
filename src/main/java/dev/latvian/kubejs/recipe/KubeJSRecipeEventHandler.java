package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.recipe.minecraft.*;

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
			event.register("minecraft:smithing", SmithingRecipeJS::new);
		});
	}
}