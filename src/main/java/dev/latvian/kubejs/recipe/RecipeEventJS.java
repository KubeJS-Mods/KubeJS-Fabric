package dev.latvian.kubejs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.core.RecipeManagerKJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerEventJS;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.util.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class RecipeEventJS extends ServerEventJS {
	public static RecipeEventJS instance;
	
	private static final Predicate<RecipeJS> ALWAYS_TRUE = r -> true;
	private static final Predicate<RecipeJS> ALWAYS_FALSE = r -> false;
	
	public final Map<Identifier, RecipeTypeJS> typeMap;
	public final List<RecipeJS> originalRecipes;
	private final List<RecipeJS> addedRecipes;
	private final Set<RecipeJS> removedRecipes;
	public final DynamicMapJS<Identifier, RecipeFunction> functionMap;
	
	private final DynamicMapJS<String, DynamicMapJS<String, RecipeFunction>> recipeFunctions;
	
	public RecipeEventJS(Map<Identifier, RecipeTypeJS> t) {
		typeMap = t;
		originalRecipes = new ArrayList<>();
		
		ScriptType.SERVER.console.logger.info("Scanning recipes...");
		
		addedRecipes = new ArrayList<>();
		removedRecipes = new HashSet<>();
		functionMap = new DynamicMapJS<>(id -> {
			RecipeSerializer<?> serializer = Registry.RECIPE_SERIALIZER.get(id);
			
			if (serializer != null) {
				RecipeTypeJS typeJS = typeMap.get(id);
				return new RecipeFunction(this, id, typeJS != null ? typeJS : new CustomRecipeTypeJS(serializer));
			} else {
				return new RecipeFunction(this, id, null);
			}
		});
		
		recipeFunctions = new DynamicMapJS<>(n -> new DynamicMapJS<>(p -> functionMap.get(new Identifier(n, p))));
	}
	
	public void post(RecipeManager recipeManager, Map<Identifier, JsonObject> jsonMap) {
		ScriptType.SERVER.console.setLineNumber(true);
		
		for (Map.Entry<Identifier, JsonObject> entry : jsonMap.entrySet()) {
			Identifier recipeId = entry.getKey();
			
			if (recipeId.getPath().startsWith("_")) {
				continue; //Forge: filter anything beginning with "_" as it's used for metadata.
			}
			
			JsonObject json = entry.getValue();
			
			try {
				// Come back to conditions
//				if (!CraftingHelper.processConditions(json, "conditions"))
//				{
//					ScriptType.SERVER.console.info("Skipping loading recipe " + recipeId + " as it's conditions were not met");
//					continue;
//				}
				
				JsonElement t = json.get("type");
				
				if (!(t instanceof JsonPrimitive) || !((JsonPrimitive) t).isString()) {
					ScriptType.SERVER.console.warn("Missing or invalid recipe recipe type, expected a string in recipe " + recipeId);
					continue;
				}
				
				RecipeFunction function = functionMap.get(new Identifier(t.getAsString()));
				
				if (function.type == null) {
					ScriptType.SERVER.console.warn("Skipping loading recipe " + recipeId + " as it's type " + function.typeID + " is unknown");
					continue;
				}
				
				RecipeJS r = function.type.factory.get();
				r.id = recipeId;
				r.type = function.type;
				r.json = json;
				r.originalRecipe = function.type.serializer.read(recipeId, json);
				
				if (r.originalRecipe == null) {
					ScriptType.SERVER.console.warn("Skipping loading recipe " + r + " as it's serializer returned null");
					continue;
				}
				
				r.deserialize();
				originalRecipes.add(r);
				
				if (r.originalRecipe.isIgnoredInRecipeBook()) {
					ScriptType.SERVER.console.debug("Loaded recipe " + r + ": <dynamic>");
				} else {
					ScriptType.SERVER.console.debug("Loaded recipe " + r + ": " + r.inputItems + " -> " + r.outputItems);
				}
			} catch (Exception ex) {
				ScriptType.SERVER.console.error("Parsing error loading recipe " + recipeId + ": " + ex);
			}
		}
		
		ScriptType.SERVER.console.logger.info("Found " + originalRecipes.size() + " recipes");
		ScriptType.SERVER.console.setLineNumber(true);
		post(ScriptType.SERVER, KubeJSEvents.RECIPES);
		post(ScriptType.SERVER, "server.datapack.recipes"); // TODO: To be removed some time later
		ScriptType.SERVER.console.setLineNumber(false);
		
		Map<RecipeType<?>, Map<Identifier, Recipe<?>>> newRecipeMap = new HashMap<>();
		int removed = 0;
		int modified = 0;
		int added = 0;
		
		for (RecipeJS r : originalRecipes) {
			if (removedRecipes.contains(r)) {
				removed++;
				continue;
			}
			
			if (r.originalRecipe == null) {
				try {
					r.serialize();
					r.originalRecipe = r.type.serializer.read(r.id, r.json);
					modified++;
				} catch (Exception ex) {
					ScriptType.SERVER.console.warn("Error parsing recipe " + r + ": " + ex);
					ex.printStackTrace();
				}
			}
			
			if (r.originalRecipe != null) {
				newRecipeMap.computeIfAbsent(r.originalRecipe.getType(), type -> new HashMap<>()).put(r.id, r.originalRecipe);
			}
		}
		
		for (RecipeJS r : addedRecipes) {
			try {
				r.serialize();
				r.originalRecipe = r.type.serializer.read(r.id, r.json);
				added++;
				newRecipeMap.computeIfAbsent(r.originalRecipe.getType(), type -> new HashMap<>()).put(r.id, r.originalRecipe);
			} catch (Exception ex) {
				ScriptType.SERVER.console.warn("Error creating recipe " + r, ex);
			}
		}
		
		FabricLoader.getInstance().getEntrypoints("kubejs-set-recipes", Consumer.class).forEach(consumer -> consumer.accept(newRecipeMap));
		((RecipeManagerKJS) recipeManager).setRecipesKJS(newRecipeMap);
		ScriptType.SERVER.console.info("Added " + added + " recipes, removed " + removed + " recipes, modified " + modified + " recipes");
	}
	
	public DynamicMapJS<String, DynamicMapJS<String, RecipeFunction>> getRecipes() {
		return recipeFunctions;
	}
	
	public RecipeJS addRecipe(RecipeJS r, RecipeTypeJS type, ListJS args1) {
		addedRecipes.add(r);
		
		if (r.id == null) {
			Identifier itemId = UtilsJS.getMCID(r.outputItems.isEmpty() ? EmptyItemStackJS.INSTANCE.getId() : r.outputItems.get(0).getId());
			r.id = new Identifier(Registry.RECIPE_SERIALIZER.getId(type.serializer).getNamespace(), "kubejs_generated_" + addedRecipes.size() + "_" + itemId.getNamespace() + "_" + itemId.getPath().replace('/', '_'));
		}
		
		if (ServerSettings.instance.logAddedRecipes) {
			ScriptType.SERVER.console.info("+ " + r + ": " + r.inputItems + " -> " + r.outputItems);
		} else {
			ScriptType.SERVER.console.debug("+ " + r + ": " + r.inputItems + " -> " + r.outputItems);
		}
		
		return r;
	}
	
	public Predicate<RecipeJS> createFilter(@Nullable Object o) {
		if (o == null || o == ALWAYS_TRUE) {
			return ALWAYS_TRUE;
		} else if (o == ALWAYS_FALSE) {
			return ALWAYS_FALSE;
		}
		
		ListJS list = ListJS.orSelf(o);
		
		if (list.isEmpty()) {
			return ALWAYS_TRUE;
		} else if (list.size() > 1) {
			Predicate<RecipeJS> predicate = ALWAYS_FALSE;
			
			for (Object o1 : list) {
				Predicate<RecipeJS> p = createFilter(o1);
				
				if (p == ALWAYS_TRUE) {
					return ALWAYS_TRUE;
				} else if (p != ALWAYS_FALSE) {
					predicate = predicate.or(p);
				}
			}
			
			return predicate;
		}
		
		MapJS map = MapJS.of(list.get(0));
		
		if (map == null || map.isEmpty()) {
			return ALWAYS_TRUE;
		}
		
		boolean exact = Boolean.TRUE.equals(map.get("exact"));
		
		Predicate<RecipeJS> predicate = ALWAYS_TRUE;
		
		if (map.get("or") != null) {
			predicate = predicate.and(createFilter(map.get("or")));
		}
		
		if (map.get("id") != null) {
			Identifier id = UtilsJS.getMCID(map.get("id").toString());
			predicate = predicate.and(recipe -> recipe.id.equals(id));
		}
		
		if (map.get("type") != null) {
			Identifier type = UtilsJS.getMCID(map.get("type").toString());
			predicate = predicate.and(recipe -> type.equals(Registry.RECIPE_SERIALIZER.getId(recipe.type.serializer)));
		}
		
		if (map.get("group") != null) {
			String group = map.get("group").toString();
			predicate = predicate.and(recipe -> recipe.getGroup().equals(group));
		}
		
		if (map.get("mod") != null) {
			String mod = map.get("mod").toString();
			predicate = predicate.and(recipe -> recipe.id.getNamespace().equals(mod));
		}
		
		if (map.get("input") != null) {
			IngredientJS in = IngredientJS.of(map.get("input"));
			predicate = predicate.and(recipe -> recipe.hasInput(in, exact));
		}
		
		if (map.get("output") != null) {
			IngredientJS out = IngredientJS.of(map.get("output"));
			predicate = predicate.and(recipe -> recipe.hasOutput(out, exact));
		}
		
		return predicate;
	}
	
	public Predicate<RecipeJS> customFilter(Predicate<RecipeJS> filter) {
		return filter;
	}
	
	public void forEachRecipe(@Nullable Object o, Consumer<RecipeJS> consumer) {
		Predicate<RecipeJS> filter = createFilter(o);
		
		if (filter == ALWAYS_TRUE) {
			originalRecipes.forEach(consumer);
		} else if (filter != ALWAYS_FALSE) {
			originalRecipes.stream().filter(filter).forEach(consumer);
		}
	}
	
	public int remove(Object filter) {
		int[] count = new int[1];
		forEachRecipe(filter, r -> {
			if (removedRecipes.add(r)) {
				if (ServerSettings.instance.logRemovedRecipes) {
					ScriptType.SERVER.console.info("- " + r + ": " + r.inputItems + " -> " + r.outputItems);
				} else {
					ScriptType.SERVER.console.debug("- " + r + ": " + r.inputItems + " -> " + r.outputItems);
				}
				
				count[0]++;
			}
		});
		return count[0];
	}
	
	public int replaceInput(Object filter, Object ingredient, Object with, boolean exact) {
		int[] count = new int[1];
		IngredientJS i = IngredientJS.of(ingredient);
		IngredientJS w = IngredientJS.of(with);
		String is = i.toString();
		String ws = w.toString();
		forEachRecipe(filter, r -> {
			if (r.replaceInput(i, w, exact)) {
				count[0]++;
				
				if (ServerSettings.instance.logAddedRecipes || ServerSettings.instance.logRemovedRecipes) {
					ScriptType.SERVER.console.info("~ " + r + ": OUT " + is + " -> " + ws);
				}
			}
		});
		return count[0];
	}
	
	public int replaceInput(Object filter, Object ingredient, Object with) {
		return replaceInput(filter, ingredient, with, false);
	}
	
	public int replaceInput(Object ingredient, Object with) {
		return replaceInput(ALWAYS_TRUE, ingredient, with);
	}
	
	public int replaceOutput(Object filter, Object ingredient, Object with, boolean exact) {
		int[] count = new int[1];
		IngredientJS i = IngredientJS.of(ingredient);
		ItemStackJS w = ItemStackJS.of(with);
		String is = i.toString();
		String ws = w.toString();
		forEachRecipe(filter, r -> {
			if (r.replaceOutput(i, w, exact)) {
				count[0]++;
				
				//if (ServerSettings.instance.logAddedRecipes || ServerSettings.instance.logRemovedRecipes) {
					ScriptType.SERVER.console.info("~ " + r + ": IN " + is + " -> " + ws);
				//}
			}
		});
		return count[0];
	}
	
	public int replaceOutput(Object filter, Object ingredient, Object with) {
		return replaceOutput(filter, ingredient, with, false);
	}
	
	public int replaceOutput(Object ingredient, Object with) {
		return replaceOutput(ALWAYS_TRUE, ingredient, with);
	}
	
	public RecipeFunction getShaped() {
		return functionMap.get(Registry.RECIPE_SERIALIZER.getId(RecipeSerializer.SHAPED));
	}
	
	public RecipeFunction getShapeless() {
		return functionMap.get(Registry.RECIPE_SERIALIZER.getId(RecipeSerializer.SHAPELESS));
	}
	
	public RecipeFunction getSmelting() {
		return functionMap.get(Registry.RECIPE_SERIALIZER.getId(RecipeSerializer.SMELTING));
	}
	
	public RecipeFunction getBlasting() {
		return functionMap.get(Registry.RECIPE_SERIALIZER.getId(RecipeSerializer.BLASTING));
	}
	
	public RecipeFunction getSmoking() {
		return functionMap.get(Registry.RECIPE_SERIALIZER.getId(RecipeSerializer.SMOKING));
	}
	
	public RecipeFunction getCampfireCooking() {
		return functionMap.get(Registry.RECIPE_SERIALIZER.getId(RecipeSerializer.CAMPFIRE_COOKING));
	}
	
	public void printTypes() {
		ScriptType.SERVER.console.info("== All recipe types ==");
		HashSet<String> list = new HashSet<>();
		originalRecipes.forEach(r -> list.add(r.type.toString()));
		list.stream().sorted().forEach(ScriptType.SERVER.console::info);
		ScriptType.SERVER.console.info(list.size() + " types");
	}
	
	public void printExamples(String type) {
		List<RecipeJS> list = originalRecipes.stream().filter(recipeJS -> recipeJS.type.toString().equals(type)).collect(Collectors.toList());
		Collections.shuffle(list);
		
		ScriptType.SERVER.console.info("== Random examples of '" + type + "' ==");
		
		for (int i = 0; i < Math.min(list.size(), 5); i++) {
			RecipeJS r = list.get(i);
			ScriptType.SERVER.console.info("- " + r.id + ":\n" + JsonUtilsJS.toPrettyString(r.json));
		}
	}
}