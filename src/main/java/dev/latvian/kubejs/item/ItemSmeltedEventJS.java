package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemSmeltedEventJS extends EventJS
{
	public final Inventory inventory;
	public final ItemStack result;

	public ItemSmeltedEventJS(Inventory inventory, ItemStack result)
	{
		this.inventory = inventory;
		this.result = result;
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(result);
	}
}