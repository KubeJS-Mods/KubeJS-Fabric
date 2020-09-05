package dev.latvian.kubejs.compat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.util.ListJS;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtOps;

import java.util.Collection;
import java.util.function.Function;

public class TechRebornRecipeEventHandler implements KubeJSInitializer {
	@Override
	public void onKubeJSInitialization() {
		if (!FabricLoader.getInstance().isModLoaded("techreborn")) return;
		RegisterRecipeHandlersEvent.EVENT.register(event -> {
			event.register("techreborn:alloy_smelter", () -> new ImplementedRecipeJS("Alloy Smelter"));
			event.register("techreborn:assembling_machine", () -> new ImplementedRecipeJS("Assembling Machine"));
			event.register("techreborn:centrifuge", () -> new ImplementedRecipeJS("Centrifuge"));
			event.register("techreborn:chemical_reactor", () -> new ImplementedRecipeJS("Chemical Reactor"));
			event.register("techreborn:compressor", () -> new ImplementedRecipeJS("Compressor"));
			event.register("techreborn:distillation_tower", () -> new ImplementedRecipeJS("Distillation Tower"));
			event.register("techreborn:extractor", () -> new ImplementedRecipeJS("Extractor"));
			event.register("techreborn:grinder", () -> new ImplementedRecipeJS("Grinder"));
			event.register("techreborn:implosion_compressor", () -> new ImplementedRecipeJS("Implosion Compressor"));
			event.register("techreborn:industrial_electrolyzer", () -> new ImplementedRecipeJS("Industrial Electrolyzer"));
			event.register("techreborn:recycler", () -> new ImplementedRecipeJS("Recycler"));
			event.register("techreborn:scrapbox", () -> new ImplementedRecipeJS("Scrapbox"));
			event.register("techreborn:vacuum_freezer", () -> new ImplementedRecipeJS("Vacuum Freezer"));
			event.register("techreborn:solid_canning_machine", () -> new ImplementedRecipeJS("Solid Canning Machine"));
			event.register("techreborn:wire_mill", () -> new ImplementedRecipeJS("Wire Mill"));
		});
	}
	
	private static abstract class SimpleRecipeJS extends RecipeJS {
		private int time = 30;
		private int power = 280;
		
		@Override
		public void create(ListJS args) {
			if (args.size() < 2) {
				throw new RecipeExceptionJS(getTypeName() + " recipe requires 2 arguments - output, and input!");
			}
			ListJS outputList = ListJS.orSelf(args.get(0));
			for (Object o : outputList) {
				ItemStackJS ingredient = ItemStackJS.of(o);
				outputItems.add(ingredient);
			}
			ListJS inputList = ListJS.orSelf(args.get(1));
			for (Object o : inputList) {
				IngredientJS ingredient = IngredientJS.of(o);
				inputItems.add(ingredient);
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
			for (Object o : outputList) {
				ItemStackJS ingredient = ItemStackJS.of(o);
				outputItems.add(ingredient);
			}
			JsonArray inputList = json.get("ingredients").getAsJsonArray();
			for (JsonElement o : inputList) {
				IngredientJS ingredient = IngredientJS.of(o);
				inputItems.add(ingredient);
			}
			
			time = json.get("time").getAsInt();
			power = json.get("power").getAsInt();
		}
		
		protected abstract String getTypeName();
		
		@Override
		public void serialize() {
			json.add("ingredients", toJsonArray(inputItems));
			json.add("results", toResultsArray(outputItems));
			json.addProperty("time", time);
			json.addProperty("power", power);
		}
	}
	
	static JsonElement toJsonArray(Collection<? extends IngredientJS> ingredients) {
		JsonArray array = new JsonArray();
		
		for (IngredientJS ingredient : ingredients) {
			if (ingredient instanceof ItemStackJS) {
				array.add(stackSerializer.apply((ItemStackJS) ingredient));
			} else array.add(ingredient.toJson());
		}
		
		return array;
	}
	
	static Function<ItemStackJS, JsonElement> stackSerializer = (stack) -> {
		JsonObject json = new JsonObject();
		json.addProperty("item", stack.getId());
		
		if (!stack.getNbt().isEmpty()) {
			json.add("nbt", Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, stack.getNbt().toNBT()));
		}
		
		if (stack.getCount() != 1) {
			json.addProperty("count", stack.getCount());
		}
		
		return json;
	};
	
	static JsonElement toResultsArray(Collection<? extends ItemStackJS> outputs) {
		JsonArray array = new JsonArray();
		
		for (ItemStackJS output : outputs) {
			array.add(stackSerializer.apply(output));
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
