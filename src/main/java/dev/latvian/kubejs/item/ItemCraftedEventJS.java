package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemCraftedEventJS extends PlayerEventJS {
	public final Player player;
	public final Container inventory;
	public final ItemStack result;
	
	public ItemCraftedEventJS(Player player, Container inventory, ItemStack result) {
		this.player = player;
		this.inventory = inventory;
		this.result = result;
	}
	
	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}
	
	public ItemStackJS getItem() {
		return ItemStackJS.of(result);
	}
	
	public InventoryJS getInventory() {
		return new InventoryJS(inventory);
	}
}