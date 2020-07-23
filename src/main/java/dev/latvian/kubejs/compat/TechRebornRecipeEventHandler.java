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

import java.util.List;
import java.util.Set;

public class TechRebornRecipeEventHandler implements KubeJSInitializer {
	@Override
	public void onKubeJSInitialization() {
		if (!FabricLoader.getInstance().isModLoaded("astromine")) return;
		RegisterRecipeHandlersEvent.EVENT.register(event -> {
			event.register(new RecipeTypeJS("techreborn:alloy_smelter", () -> new ImplementedRecipeJS("Alloy Smelter")));
			event.register(new RecipeTypeJS("techreborn:assembling_machine", () -> new ImplementedRecipeJS("Assembling Machine")));
			event.register(new RecipeTypeJS("techreborn:centrifuge", () -> new ImplementedRecipeJS("Centrifuge")));
			event.register(new RecipeTypeJS("techreborn:chemical_reactor", () -> new ImplementedRecipeJS("Chemical Reactor")));
			event.register(new RecipeTypeJS("techreborn:compressor", () -> new ImplementedRecipeJS("Compressor")));
			event.register(new RecipeTypeJS("techreborn:distillation_tower", () -> new ImplementedRecipeJS("Distillation Tower")));
			event.register(new RecipeTypeJS("techreborn:extractor", () -> new ImplementedRecipeJS("Extractor")));
			event.register(new RecipeTypeJS("techreborn:grinder", () -> new ImplementedRecipeJS("Grinder")));
			event.register(new RecipeTypeJS("techreborn:implosion_compressor", () -> new ImplementedRecipeJS("Implosion Compressor")));
			event.register(new RecipeTypeJS("techreborn:industrial_electrolyzer", () -> new ImplementedRecipeJS("Industrial Electrolyzer")));
			event.register(new RecipeTypeJS("techreborn:recycler", () -> new ImplementedRecipeJS("Recycler")));
			event.register(new RecipeTypeJS("techreborn:scrapbox", () -> new ImplementedRecipeJS("Scrapbox")));
			event.register(new RecipeTypeJS("techreborn:vacuum_freezer", () -> new ImplementedRecipeJS("Vacuum Freezer")));
			event.register(new RecipeTypeJS("techreborn:solid_canning_machine", () -> new ImplementedRecipeJS("Solid Canning Machine")));
			event.register(new RecipeTypeJS("techreborn:wire_mill", () -> new ImplementedRecipeJS("Wire Mill")));
		});
	}
	
	private static abstract class SimpleRecipeJS extends RecipeJS {
		private List<IngredientJS> inputs, outputs;
		private int time = 30;
		private int power = 280;
		
		@Override
		public void create(ListJS args) {
			if (args.size() < 2) {
				throw new RecipeExceptionJS(getTypeName() + " recipe requires 2 arguments - output, and input!");
			}
			ListJS outputList = ListJS.of(args.get(0));
			if (outputList.isEmpty()) {
				throw new RecipeExceptionJS(getTypeName() + " recipe result " + args.get(0) + " is not a valid item!");
			}
			for (Object o : outputList) {
				IngredientJS ingredient = IngredientJS.of(o);
				if (ingredient.isEmpty()) {
					throw new RecipeExceptionJS(getTypeName() + " recipe result " + o + " is not a valid item!");
				}
				inputs.add(ingredient);
			}
			ListJS inputList = ListJS.of(args.get(1));
			if (inputList.isEmpty()) {
				throw new RecipeExceptionJS(getTypeName() + " recipe input " + args.get(1) + " is not a valid item!");
			}
			for (Object o : inputList) {
				IngredientJS ingredient = IngredientJS.of(o);
				if (ingredient.isEmpty()) {
					throw new RecipeExceptionJS(getTypeName() + " recipe input " + o + " is not a valid item!");
				}
				outputs.add(ingredient);
			}
			
			if (args.size() >= 3) {
				time = ((Number) args.get(2)).intValue();
			}
			
			if (args.size() >= 4) {
				power = ((Number) args.get(3)).intValue();
			}
		}
		
		@Override
		public void deserialize() {
			ListJS outputList = ListJS.of(json.get("results"));
			if (outputList.isEmpty()) {
				throw new RecipeExceptionJS(getTypeName() + " recipe result " + json.get("results") + " is not a valid item!");
			}
			for (Object o : outputList) {
				IngredientJS ingredient = IngredientJS.of(o);
				if (ingredient.isEmpty()) {
					throw new RecipeExceptionJS(getTypeName() + " recipe result " + o + " is not a valid item!");
				}
				inputs.add(ingredient);
			}
			ListJS inputList = ListJS.of(json.get("ingredients"));
			if (inputList.isEmpty()) {
				throw new RecipeExceptionJS(getTypeName() + " recipe input " + json.get("ingredients") + " is not a valid item!");
			}
			for (Object o : inputList) {
				IngredientJS ingredient = IngredientJS.of(o);
				if (ingredient.isEmpty()) {
					throw new RecipeExceptionJS(getTypeName() + " recipe input " + o + " is not a valid item!");
				}
				outputs.add(ingredient);
			}
			
			time = json.get("time").getAsInt();
			power = json.get("power").getAsInt();
		}
		
		protected abstract String getTypeName();
		
		@Override
		public void serialize() {
			JsonArray ingredients = new JsonArray();
			for (IngredientJS input : inputs) {
				ingredients.add(toJson(input));
			}
			json.add("ingredients", ingredients);
			JsonArray results = new JsonArray();
			for (IngredientJS output : outputs) {
				results.add(toJson(output));
			}
			json.add("results", results);
			json.addProperty("time", time);
			json.addProperty("power", power);
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
	
	private static class ImplementedRecipeJS extends SimpleRecipeJS {
		private final String typeName;
		
		public ImplementedRecipeJS(String typeName) {
			this.typeName = typeName;
		}
		
		@Override
		protected String getTypeName() {
			return typeName;
		}
	}
}
