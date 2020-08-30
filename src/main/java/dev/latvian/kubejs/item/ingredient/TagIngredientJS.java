package dev.latvian.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.BoundItemStackJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class TagIngredientJS implements IngredientJS {
	private final ResourceLocation tag;
	private final int count;
	
	public TagIngredientJS(ResourceLocation t, int count) {
		this.tag = t;
		this.count = count;
	}
	
	public ResourceLocation getTag() {
		return tag;
	}
	
	@Override
	public int getCount() {
		return count;
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
		Tag<Item> t = ItemTags.getAllTags().getTag(tag);
		
		if (t != null && t.getValues().size() > 0) {
			NonNullList<ItemStack> list = NonNullList.create();
			
			for (Item item : t.getValues()) {
				item.fillItemCategory(CreativeModeTab.TAB_SEARCH, list);
			}
			
			Set<ItemStackJS> set = new LinkedHashSet<>();
			
			for (ItemStack stack1 : list) {
				ItemStack stack = stack1;
				if (!stack.isEmpty()) {
					stack = stack.copy();
					stack.setCount(count);
					set.add(new BoundItemStackJS(stack));
				}
			}
			
			return set;
		}
		
		return Collections.emptySet();
	}
	
	@Override
	public ItemStackJS getFirst() {
		Tag<Item> t = ItemTags.getAllTags().getTag(tag);
		
		if (t != null && t.getValues().size() > 0) {
			NonNullList<ItemStack> list = NonNullList.create();
			
			for (Item item : t.getValues()) {
				item.fillItemCategory(CreativeModeTab.TAB_SEARCH, list);
				
				for (ItemStack stack : list) {
					ItemStack stack1 = stack.copy();
					if (!stack1.isEmpty()) {
						stack1.setCount(count);
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
		if (ItemTags.getAllTags().getAllTags().isEmpty()) {
			return false;
		}
		
		Tag<Item> t = ItemTags.getAllTags().getTag(tag);
		return t != null && t.getValues().isEmpty();
	}
	
	@Override
	public String toString() {
		return "'#" + tag + "'";
	}
	
	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("tag", tag.toString());
		if (count != 1) {
			json.addProperty("count", getCount());
		}
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