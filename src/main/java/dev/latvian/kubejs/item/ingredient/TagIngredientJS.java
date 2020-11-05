package dev.latvian.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.BoundItemStackJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.SetTag;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LatvianModder
 */
public class TagIngredientJS implements IngredientJS {
	private static final Map<String, TagIngredientJS> tagIngredientCache = new ConcurrentHashMap<>();
	
	public static TagIngredientJS createTag(String tag) {
		return tagIngredientCache.computeIfAbsent(tag, TagIngredientJS::new);
	}
	
	public static void clearTagCache() {
		tagIngredientCache.clear();
	}
	
	private final ResourceLocation tag;
	private Tag<Item> actualTag;
	
	private TagIngredientJS(String t) {
		this.tag = new ResourceLocation(t);
	}
	
	public ResourceLocation getTag() {
		return tag;
	}
	
	public Tag<Item> getActualTag() {
		if (actualTag == null) {
			actualTag = SerializationTags.getInstance().getItems().getTag(tag);
			
			if (actualTag == null) {
				actualTag = SetTag.empty();
			}
		}
		
		return actualTag;
	}
	
	@Override
	public boolean test(ItemStackJS stack) {
		return !stack.isEmpty() && stack.getItem().is(TagRegistry.item(tag));
	}
	
	@Override
	public boolean testVanilla(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem().is(TagRegistry.item(tag));
	}
	
	@Override
	public Set<ItemStackJS> getStacks() {
		Tag<Item> t = getActualTag();
		
		if (t.getValues().size() > 0) {
			NonNullList<ItemStack> list = NonNullList.create();
			
			for (Item item : t.getValues()) {
				item.fillItemCategory(CreativeModeTab.TAB_SEARCH, list);
			}
			
			Set<ItemStackJS> set = new LinkedHashSet<>();
			
			for (ItemStack stack1 : list) {
				ItemStack stack = stack1;
				if (!stack.isEmpty()) {
					stack = stack.copy();
					set.add(new BoundItemStackJS(stack));
				}
			}
			
			return set;
		}
		
		return Collections.emptySet();
	}
	
	@Override
	public ItemStackJS getFirst() {
		Tag<Item> t = getActualTag();
		
		if (t.getValues().size() > 0) {
			NonNullList<ItemStack> list = NonNullList.create();
			
			for (Item item : t.getValues()) {
				item.fillItemCategory(CreativeModeTab.TAB_SEARCH, list);
				
				for (ItemStack stack : list) {
					ItemStack stack1 = stack.copy();
					if (!stack1.isEmpty()) {
						return new BoundItemStackJS(stack1);
					}
				}
				
				list.clear();
			}
		}
		
		return EmptyItemStackJS.INSTANCE;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public String toString() {
		return "'#" + tag + "'";
	}
	
	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("tag", tag.toString());
		return json;
	}
	
	@Override
	public boolean anyStackMatches(IngredientJS ingredient) {
		if (ingredient instanceof TagIngredientJS && tag.equals(((TagIngredientJS) ingredient).tag)) {
			return true;
		}
		
		return IngredientJS.super.anyStackMatches(ingredient);
	}
}