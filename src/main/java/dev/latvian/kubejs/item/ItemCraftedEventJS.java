package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemCraftedEventJS extends PlayerEventJS
{
	public final PlayerEntity player;
	public final Inventory inventory;
	public final ItemStack result;

	public ItemCraftedEventJS(PlayerEntity player, Inventory inventory, ItemStack result)
	{
		this.player = player;
		this.inventory = inventory;
		this.result = result;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(player);
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(result);
	}

	public InventoryJS getInventory()
	{
		return new InventoryJS(inventory);
	}
}