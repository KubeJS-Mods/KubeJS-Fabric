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
public class TagIngredientJS implements IngredientJS
{
	private final Identifier tag;

	public TagIngredientJS(Identifier t)
	{
		tag = t;
	}

	public Identifier getTag()
	{
		return tag;
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return !stack.isEmpty() && stack.getItem().isIn(TagRegistry.item(tag));
	}

	@Override
	public boolean testVanilla(ItemStack stack)
	{
		return !stack.isEmpty() && stack.getItem().isIn(TagRegistry.item(tag));
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		Tag<Item> t = ItemTags.getContainer().get(tag);

		if (t != null && t.values().size() > 0)
		{
			DefaultedList<ItemStack> list = DefaultedList.of();

			for (Item item : t.values())
			{
				item.appendStacks(ItemGroup.SEARCH, list);
			}

			Set<ItemStackJS> set = new LinkedHashSet<>();

			for (ItemStack stack1 : list)
			{
				if (!stack1.isEmpty())
				{
					set.add(new BoundItemStackJS(stack1));
				}
			}

			return set;
		}

		return Collections.emptySet();
	}

	@Override
	public ItemStackJS getFirst()
	{
		Tag<Item> t = ItemTags.getContainer().get(tag);

		if (t != null && t.values().size() > 0)
		{
			DefaultedList<ItemStack> list = DefaultedList.of();

			for (Item item : t.values())
			{
				item.appendStacks(ItemGroup.SEARCH, list);

				for (ItemStack stack : list)
				{
					if (!stack.isEmpty())
					{
						return new BoundItemStackJS(stack);
					}
				}

				list.clear();
			}
		}

		return EmptyItemStackJS.INSTANCE;
	}

	@Override
	public boolean isEmpty()
	{
		if (ItemTags.getContainer().getEntries().isEmpty())
		{
			return false;
		}

		Tag<Item> t = ItemTags.getContainer().get(tag);
		return t != null && t.values().isEmpty();
	}

	@Override
	public String toString()
	{
		return "'#" + tag + "'";
	}

	@Override
	public JsonElement toJson()
	{
		JsonObject json = new JsonObject();
		json.addProperty("tag", tag.toString());
		return json;
	}

	@Override
	public boolean anyStackMatches(IngredientJS ingredient)
	{
		if (ingredient instanceof TagIngredientJS && tag.equals(((TagIngredientJS) ingredient).tag))
		{
			return true;
		}

		return IngredientJS.super.anyStackMatches(ingredient);
	}
}