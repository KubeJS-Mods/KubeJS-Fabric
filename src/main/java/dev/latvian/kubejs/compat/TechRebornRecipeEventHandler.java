package dev.latvian.kubejs.compat;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.util.ListJS;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		private List<IngredientJS> inputs = new ArrayList<>();
		private List<IngredientJS> outputs = new ArrayList<>();
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
				if (ingredient instanceof ItemStackJS){ outputItems.add((ItemStackJS) ingredient);}
				else outputs.add(ingredient);
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
				if (ingredient instanceof ItemStackJS) inputItems.add(ingredient);
				else inputs.add(ingredient);
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
				if (ingredient instanceof ItemStackJS) outputItems.add((ItemStackJS) ingredient);
				else outputs.add(ingredient);
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
				if (ingredient instanceof ItemStackJS) inputItems.add(ingredient);
				else inputs.add(ingredient);
			}

			time = json.get("time").getAsInt();
			power = json.get("power").getAsInt();
		}

		protected abstract String getTypeName();

		@Override
		public void serialize() {
			json.add("ingredients", toJsonArray(Stream.concat(inputs.stream(), inputItems.stream()).collect(Collectors.toSet())));
			json.add("results", toJsonArray(Stream.concat(outputs.stream(), outputItems.stream()).collect(Collectors.toSet())));
			json.addProperty("time", time);
			json.addProperty("power", power);
		}
	}
	
	static JsonElement toJsonArray(Collection<? extends IngredientJS> ingredients) {
		JsonArray array = new JsonArray();

		ingredients.forEach(ingredient -> {
			array.add(ingredient.toJson());
		});

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
