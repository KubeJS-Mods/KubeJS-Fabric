package dev.latvian.kubejs.recipe;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static net.minecraft.world.item.crafting.RecipeManager.fromJson;

/**
 * @author LatvianModder
 */
public class RecipeEventJS extends ServerEventJS {
	public static RecipeEventJS instance;
	
	private static final Predicate<RecipeJS> ALWAYS_TRUE = r -> true;
	private static final Predicate<RecipeJS> ALWAYS_FALSE = r -> false;
	
	public final Map<ResourceLocation, RecipeTypeJS> typeMap;
	public final List<Recipe<?>> failedRecipes;
	public final List<RecipeJS> originalRecipes;
	private final List<RecipeJS> addedRecipes;
	private final Set<RecipeJS> removedRecipes;
	public final Map<ResourceLocation, RecipeFunction> functionMap;
	
	private final DynamicMapJS<String, DynamicMapJS<String, RecipeFunction>> recipeFunctions;
	
	public RecipeEventJS(Map<ResourceLocation, RecipeTypeJS> t) {
		typeMap = t;
		failedRecipes = new ArrayList<>();
		originalRecipes = new ArrayList<>();
		
		ScriptType.SERVER.console.getLogger().info("Scanning recipes...");
		
		addedRecipes = new ArrayList<>();
		removedRecipes = new HashSet<>();
		functionMap = new DynamicConcurrentMapJS<>(id -> {
			RecipeSerializer<?> serializer = Registry.RECIPE_SERIALIZER.get(id);
			
			if (serializer != null) {
				RecipeTypeJS typeJS = typeMap.get(id);
				return new RecipeFunction(this, id, typeJS != null ? typeJS : new CustomRecipeTypeJS(serializer));
			} else {
				return new RecipeFunction(this, id, null);
			}
		});
		
		recipeFunctions = new DynamicMapJS<>(n -> new DynamicMapJS<>(p -> functionMap.get(new ResourceLocation(n, p))));
	}
	
	public void post(RecipeManager recipeManager, Map<ResourceLocation, JsonObject> jsonMap) {
		ScriptType.SERVER.console.setLineNumber(true);
		Stopwatch timer = Stopwatch.createUnstarted();
		
		List<CompletableFuture<Pair<List<RecipeJS>, List<Recipe<?>>>>> completableFutures = Lists.newArrayList();
		timer.reset().start();
		Iterables.partition(jsonMap.entrySet(), 100)
				.forEach(entries -> completableFutures.add(CompletableFuture.supplyAsync(() -> {
					List<RecipeJS> originalRecipes = Lists.newArrayList();
					List<Recipe<?>> failedRecipes = Lists.newArrayList();
					
					for (Map.Entry<ResourceLocation, JsonObject> entry : entries) {
						ResourceLocation recipeId = entry.getKey();
						JsonObject json = entry.getValue();
						
						if (recipeId.getPath().startsWith("_")) {
							continue; //Forge: filter anything beginning with "_" as it's used for metadata.
						}
						
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
							
							RecipeFunction function = functionMap.get(new ResourceLocation(t.getAsString()));
							
							if (function.type == null) {
								ScriptType.SERVER.console.warn("Skipping loading recipe " + recipeId + " as it's type " + function.typeID + " is unknown");
								continue;
							}
							
							RecipeJS recipe = function.type.factory.get();
							recipe.id = recipeId;
							recipe.type = function.type;
							recipe.json = json;
							recipe.originalRecipe = function.type.serializer.fromJson(recipeId, json);
							
							if (recipe.originalRecipe == null) {
								ScriptType.SERVER.console.warn("Skipping loading recipe " + recipe + " as it's serializer returned null");
								continue;
							}
							
							recipe.deserialize();
							originalRecipes.add(recipe);
							
							if (recipe.originalRecipe.isSpecial()) {
								ScriptType.SERVER.console.debug("Loaded recipe " + recipe + ": <dynamic>");
							} else {
								ScriptType.SERVER.console.debug("Loaded recipe " + recipe + ": " + recipe.inputItems + " -> " + recipe.outputItems);
							}
						} catch (Exception ex) {
							ScriptType.SERVER.console.warn("Parsing error loading recipe " + recipeId, ex);
							try {
								Recipe<?> recipe = fromJson(recipeId, GsonHelper.convertToJsonObject(entry.getValue(), "top element"));
								failedRecipes.add(recipe);
							} catch (IllegalArgumentException | JsonParseException ex2) {
								ScriptType.SERVER.console.warn("Parsing error loading recipe " + recipeId, ex2);
							}
						}
					}
					
					return new Pair<>(originalRecipes, failedRecipes);
				})));
		
		try {
			CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		for (CompletableFuture<Pair<List<RecipeJS>, List<Recipe<?>>>> future : completableFutures) {
			Pair<List<RecipeJS>, List<Recipe<?>>> now = future.getNow(null);
			if (now != null) {
				originalRecipes.addAll(now.getFirst());
				failedRecipes.addAll(now.getSecond());
			}
		}
		
		
		ScriptType.SERVER.console.getLogger().info("Found " + originalRecipes.size() + " recipes and " + failedRecipes.size() + "failed recipes in " + timer.stop());
		timer.reset().start();
		ScriptType.SERVER.console.setLineNumber(true);
		post(ScriptType.SERVER, KubeJSEvents.RECIPES);
		ScriptType.SERVER.console.setLineNumber(false);
		ScriptType.SERVER.console.getLogger().info("Posted recipe events in " + timer.stop());
		
		Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newRecipeMap = new HashMap<>();
		int[] removed = {0};
		int[] modified = {0};
		int[] added = {0};
		
		timer.reset().start();
		originalRecipes.parallelStream()
				.filter(recipe -> {
					if (removedRecipes.contains(recipe)) {
						removed[0]++;
						return false;
					}
					return true;
				})
				.map(recipe -> {
					if (recipe.originalRecipe != null) {
						try {
							recipe.serialize();
							return recipe.originalRecipe = recipe.type.serializer.fromJson(recipe.id, recipe.json);
						} catch (Exception ex) {
							ScriptType.SERVER.console.warn("Error parsing recipe " + recipe + ": " + recipe.json, ex);
						}
					}
					return recipe.originalRecipe;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(Recipe::getType,
						Collectors.groupingBy(Recipe::getId,
								Collectors.reducing(null, UnaryOperator.identity(), (recipe, recipe2) -> recipe2))))
				.forEach((recipeType, map) -> {
					modified[0] += map.size();
					newRecipeMap.computeIfAbsent(recipeType, type -> new HashMap<>()).putAll(map);
				});
		failedRecipes.parallelStream()
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(Recipe::getType,
						Collectors.groupingBy(Recipe::getId,
								Collectors.reducing(null, UnaryOperator.identity(), (recipe, recipe2) -> recipe2))))
				.forEach((recipeType, map) -> {
					modified[0] += map.size();
					newRecipeMap.computeIfAbsent(recipeType, type -> new HashMap<>()).putAll(map);
				});
		ScriptType.SERVER.console.getLogger().info("Modified & removed recipes in " + timer.stop());
		
		timer.reset().start();
		addedRecipes.parallelStream()
				.map(recipe -> {
					try {
						recipe.serialize();
						return recipe.originalRecipe = recipe.type.serializer.fromJson(recipe.id, recipe.json);
					} catch (Exception ex) {
						ScriptType.SERVER.console.warn("Error creating recipe " + recipe + ": " + recipe.json, ex);
					}
					return null;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(Recipe::getType,
						Collectors.groupingBy(Recipe::getId,
								Collectors.reducing(null, UnaryOperator.identity(), (recipe, recipe2) -> recipe2))))
				.forEach((recipeType, map) -> {
					added[0] += map.size();
					newRecipeMap.computeIfAbsent(recipeType, type -> new HashMap<>()).putAll(map);
				});
		ScriptType.SERVER.console.getLogger().info("Added recipes in " + timer.stop());
		
		FabricLoader.getInstance().getEntrypoints("kubejs-set-recipes", Consumer.class).forEach(consumer -> consumer.accept(newRecipeMap));
		((RecipeManagerKJS) recipeManager).setRecipesKJS(newRecipeMap);
		ScriptType.SERVER.console.info("Added " + added[0] + " recipes, removed " + removed[0] + " recipes, modified " + modified[0] + " recipes");
	}
	
	public DynamicMapJS<String, DynamicMapJS<String, RecipeFunction>> getRecipes() {
		return recipeFunctions;
	}
	
	public RecipeJS addRecipe(RecipeJS r, RecipeTypeJS type, ListJS args1) {
		addedRecipes.add(r);
		
		if (r.id == null) {
			ResourceLocation itemId = UtilsJS.getMCID(r.outputItems.isEmpty() ? EmptyItemStackJS.INSTANCE.getId() : r.outputItems.get(0).getId());
			r.id = new ResourceLocation(Registry.RECIPE_SERIALIZER.getKey(type.serializer).getNamespace(), "kubejs_generated_" + addedRecipes.size() + "_" + itemId.getNamespace() + "_" + itemId.getPath().replace('/', '_'));
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
			ResourceLocation id = UtilsJS.getMCID(map.get("id").toString());
			predicate = predicate.and(recipe -> recipe.id.equals(id));
		}
		
		if (map.get("type") != null) {
			ResourceLocation type = UtilsJS.getMCID(map.get("type").toString());
			predicate = predicate.and(recipe -> type.equals(Registry.RECIPE_SERIALIZER.getKey(recipe.type.serializer)));
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
				
				if (ServerSettings.instance.logAddedRecipes || ServerSettings.instance.logRemovedRecipes) {
					ScriptType.SERVER.console.info("~ " + r + ": IN " + is + " -> " + ws);
				}
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
		return functionMap.get(Registry.RECIPE_SERIALIZER.getKey(RecipeSerializer.SHAPED_RECIPE));
	}
	
	public RecipeFunction getShapeless() {
		return functionMap.get(Registry.RECIPE_SERIALIZER.getKey(RecipeSerializer.SHAPELESS_RECIPE));
	}
	
	public RecipeFunction getSmelting() {
		return functionMap.get(Registry.RECIPE_SERIALIZER.getKey(RecipeSerializer.SMELTING_RECIPE));
	}
	
	public RecipeFunction getBlasting() {
		return functionMap.get(Registry.RECIPE_SERIALIZER.getKey(RecipeSerializer.BLASTING_RECIPE));
	}
	
	public RecipeFunction getSmoking() {
		return functionMap.get(Registry.RECIPE_SERIALIZER.getKey(RecipeSerializer.SMOKING_RECIPE));
	}
	
	public RecipeFunction getCampfireCooking() {
		return functionMap.get(Registry.RECIPE_SERIALIZER.getKey(RecipeSerializer.CAMPFIRE_COOKING_RECIPE));
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