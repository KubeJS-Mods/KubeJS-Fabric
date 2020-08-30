package dev.latvian.kubejs.item;

import dev.latvian.kubejs.event.EventJS;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemSmeltedEventJS extends EventJS {
	public final Container inventory;
	public final ItemStack result;
	
	public ItemSmeltedEventJS(Container inventory, ItemStack result) {
		this.inventory = inventory;
		this.result = result;
	}
	
	public ItemStackJS getItem() {
		return ItemStackJS.of(result);
	}
}