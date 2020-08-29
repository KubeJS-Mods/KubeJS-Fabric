package dev.latvian.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.BoundItemStackJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class TagIngredientJS implements IngredientJS {
	private final Identifier tag;
	private final int count;
	
	public TagIngredientJS(Identifier t, int count) {
		this.tag = t;
		this.count = count;
	}
	
	public Identifier getTag() {
		return tag;
	}
	
	@Override
	public int getCount() {
		return count;
	}
	
	@Override
	public boolean test(ItemStackJS stack) {
		return !stack.isEmpty() && stack.getItem().isIn(TagRegistry.item(tag));
	}
	
	@Override
	public boolean testVanilla(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem().isIn(TagRegistry.item(tag));
	}
	
	@Override
	public Set<ItemStackJS> getStacks() {
		Tag<Item> t = ItemTags.getTagGroup().getTag(tag);
		
		if (t != null && t.values().size() > 0) {
			DefaultedList<ItemStack> list = DefaultedList.of();
			
			for (Item item : t.values()) {
				item.appendStacks(ItemGroup.SEARCH, list);
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
		Tag<Item> t = ItemTags.getTagGroup().getTag(tag);
		
		if (t != null && t.values().size() > 0) {
			DefaultedList<ItemStack> list = DefaultedList.of();
			
			for (Item item : t.values()) {
				item.appendStacks(ItemGroup.SEARCH, list);
				
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
		if (ItemTags.getTagGroup().getTags().isEmpty()) {
			return false;
		}
		
		Tag<Item> t = ItemTags.getTagGroup().getTag(tag);
		return t != null && t.values().isEmpty();
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