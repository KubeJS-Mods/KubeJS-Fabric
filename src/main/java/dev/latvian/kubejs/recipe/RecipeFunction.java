package dev.latvian.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.TagIngredientJS;
import dev.latvian.kubejs.recipe.minecraft.CustomRecipeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.WrappedJS;
import jdk.nashorn.api.scripting.AbstractJSObject;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class RecipeFunction extends AbstractJSObject implements WrappedJS {
	private final RecipeEventJS event;
	public final Identifier typeID;
	public final RecipeTypeJS type;
	
	public RecipeFunction(RecipeEventJS e, Identifier id, @Nullable RecipeTypeJS t) {
		event = e;
		typeID = id;
		type = t;
	}
	
	@Override
	public RecipeJS call(Object thiz, Object... args0) {
		try {
			if (type == null) {
				KubeJS.LOGGER.log(Level.WARN, "Unknown recipe type!");
			}
			
			ListJS args = ListJS.of(args0);
			
			if (args == null || args.isEmpty()) {
				KubeJS.LOGGER.log(Level.WARN, "Recipe requires at least one argument!");
			} else if (type.isCustom() && args.size() != 1) {
				KubeJS.LOGGER.log(Level.WARN, "Custom recipe has to use a single JSON object argument!");
			}
			
			if (args.size() == 1) {
				MapJS map = MapJS.of(args.get(0));
				
				if (map != null) {
					RecipeJS recipe = type.factory.get();
					recipe.type = type;
					recipe.json = ((MapJS) normalize(map)).toJson();
					recipe.deserialize();
					return event.addRecipe(recipe, type, args);
				} else {
					KubeJS.LOGGER.log(Level.WARN, "One argument recipes have to be a JSON object!");
				}
			}
			
			RecipeJS recipe = type.factory.get();
			recipe.type = type;
			recipe.json = new JsonObject();
			recipe.create(args);
			return event.addRecipe(recipe, type, args);
		} catch (RecipeExceptionJS ex) {
			ScriptType.SERVER.console.warn("Failed to create recipe for type '" + typeID + "': " + ex);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return new CustomRecipeJS();
	}
	
	private Object normalize(Object o) {
		if (o instanceof ItemStackJS) {
			return ((ItemStackJS) o).toResultJson();
		} else if (o instanceof IngredientJS) {
			return ((IngredientJS) o).toJson();
		} else if (o instanceof String) {
			String s = (String) o;
			
			if (s.length() >= 4 && s.startsWith("#") && s.indexOf(':') != -1) {
				return new TagIngredientJS(new Identifier(s.substring(1)), 1).toJson();
			}
			
			return o;
		} else if (o instanceof ListJS) {
			ListJS list = new ListJS();
			
			for (Object o1 : (ListJS) o) {
				list.add(normalize(o1));
			}
			
			return list;
		} else if (o instanceof MapJS) {
			MapJS map = new MapJS();
			
			for (Map.Entry<String, Object> entry : ((MapJS) o).entrySet()) {
				map.put(entry.getKey(), normalize(entry.getValue()));
			}
			
			return map;
		}
		
		return o;
	}
	
	@Override
	public String toString() {
		return typeID.toString();
	}
}