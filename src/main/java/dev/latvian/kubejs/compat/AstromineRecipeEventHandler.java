package dev.latvian.kubejs.compat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.util.ListJS;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Set;

public class AstromineRecipeEventHandler implements KubeJSInitializer {
	@Override
	public void onKubeJSInitialization() {
		if (!FabricLoader.getInstance().isModLoaded("astromine")) return;
		RegisterRecipeHandlersEvent.EVENT.register(event -> {
			event.register(new RecipeTypeJS("astromine:triturating", TrituratingRecipeJS::new));
			event.register(new RecipeTypeJS("astromine:pressing", PressingRecipeJS::new));
			event.register(new RecipeTypeJS("astromine:alloy_smelting", AlloySmelterRecipeJS::new));
		});
	}
	
	private static abstract class SimpleRecipeJS extends RecipeJS {
		private IngredientJS input, output;
		private int time = 30;
		private double energyConsumed = 280;
		
		@Override
		public void create(ListJS args) {
			if (args.size() < 2) {
				throw new RecipeExceptionJS(getTypeName() + " recipe requires 2 arguments - output, and input!");
			}
			output = IngredientJS.of(args.get(0));
			if (output.isEmpty()) {
				throw new RecipeExceptionJS(getTypeName() + " recipe result " + args.get(0) + " is not a valid item!");
			}
			input = IngredientJS.of(args.get(1));
			if (input.isEmpty()) {
				throw new RecipeExceptionJS(getTypeName() + " recipe input " + args.get(1) + " is not a valid item!");
			}
			
			if (args.size() >= 3) {
				time = ((Number) args.get(2)).intValue();
			}
			
			if (args.size() >= 4) {
				energyConsumed = ((Number) args.get(3)).doubleValue();
			}
		}
		
		@Override
		public void deserialize() {
			input = IngredientJS.ingredientFromRecipeJson(json.get("input"));
			output = ItemStackJS.resultFromRecipeJson(json.get("output"));
			
			time = json.get("time").getAsInt();
			energyConsumed = json.get("energy_consumed").getAsDouble();
		}
		
		protected abstract String getTypeName();
		
		@Override
		public void serialize() {
			json.add("input", toJson(input));
			json.add("output", toJson(output));
			json.addProperty("time", time);
			json.addProperty("energy_consumed", energyConsumed);
		}
	}
	
	private static class AlloySmelterRecipeJS extends RecipeJS {
		private IngredientJS firstInput, secondInput, output;
		private int time = 200;
		private double energyConsumed = 800;
		
		@Override
		public void create(ListJS args) {
			if (args.size() < 3) {
				throw new RecipeExceptionJS(getTypeName() + " recipe requires 2 arguments - output, first input, and second input!");
			}
			output = IngredientJS.of(args.get(0));
			if (output.isEmpty()) {
				throw new RecipeExceptionJS(getTypeName() + " recipe result " + args.get(0) + " is not a valid item!");
			}
			firstInput = IngredientJS.of(args.get(1));
			if (firstInput.isEmpty()) {
				throw new RecipeExceptionJS(getTypeName() + " recipe first input " + args.get(1) + " is not a valid item!");
			}
			secondInput = IngredientJS.of(args.get(2));
			if (secondInput.isEmpty()) {
				throw new RecipeExceptionJS(getTypeName() + " recipe second input " + args.get(2) + " is not a valid item!");
			}
			
			if (args.size() >= 4) {
				time = ((Number) args.get(3)).intValue();
			}
			
			if (args.size() >= 5) {
				energyConsumed = ((Number) args.get(4)).doubleValue();
			}
		}
		
		@Override
		public void deserialize() {
			firstInput = IngredientJS.of(json.get("firstInput"));
			if (firstInput.isEmpty()) {
				throw new RecipeExceptionJS(getTypeName() + " recipe first input " + json.get("firstInput") + " is not a valid item!");
			}
			
			secondInput = IngredientJS.of(json.get("secondInput"));
			if (secondInput.isEmpty()) {
				throw new RecipeExceptionJS(getTypeName() + " recipe second input " + json.get("secondInput") + " is not a valid item!");
			}
			
			output = IngredientJS.of(json.get("output"));
			if (output.isEmpty()) {
				throw new RecipeExceptionJS(getTypeName() + " recipe result " + json.get("output") + " is not a valid item!");
			}
			
			time = json.get("time").getAsInt();
			energyConsumed = json.get("energy_consumed").getAsDouble();
		}
		
		protected String getTypeName() {
			return "Alloy Smelter";
		}
		
		@Override
		public void serialize() {
			json.add("firstInput", toJson(firstInput));
			json.add("secondInput", toJson(secondInput));
			json.add("output", toJson(output));
			json.addProperty("time", time);
			json.addProperty("energy_consumed", energyConsumed);
		}
	}
	
	static JsonElement toJson(IngredientJS ingredient) {
		Set<ItemStackJS> set = ingredient.getStacks();
		
		if (set.size() == 1) {
			return set.iterator().next().toResultJson();
		}
		
		JsonArray array = new JsonArray();
		
		for (ItemStackJS stackJS : set) {
			array.add(stackJS.toResultJson());
		}
		
		return array;
	}
	
	private static class TrituratingRecipeJS extends SimpleRecipeJS {
		
		@Override
		protected String getTypeName() {
			return "Triturating";
		}
	}
	
	private static class PressingRecipeJS extends SimpleRecipeJS {
		
		@Override
		protected String getTypeName() {
			return "Pressing";
		}
	}
}
