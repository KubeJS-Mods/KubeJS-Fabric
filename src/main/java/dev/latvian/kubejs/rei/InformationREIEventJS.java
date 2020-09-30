package dev.latvian.kubejs.rei;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.ListJS;
import me.shedaniel.rei.api.BuiltinPlugin;
import me.shedaniel.rei.api.EntryStack;

import java.util.stream.Collectors;

public class InformationREIEventJS extends EventJS {
	public void add(Object stacks, Object title, Object description) {
		BuiltinPlugin.getInstance().registerInformation(
				EntryStack.ofItemStacks(IngredientJS.of(stacks).getStacks().stream().map(ItemStackJS::getItemStack).collect(Collectors.toList())),
				Text.of(title).component(),
				components -> {
					ListJS.orSelf(description).stream().map(Text::of).map(Text::component).collect(Collectors.toCollection(() -> components));
					return components;
				}
		);
	}
}