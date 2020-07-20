package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface ItemCraftCallback {
	Event<ItemCraftCallback> EVENT = EventFactory.createArrayBacked(ItemCraftCallback.class, (listeners) -> {
		return (player, inventory, result) -> {
			for (ItemCraftCallback listener : listeners) {
				listener.craft(player, inventory, result);
			}
		};
	});
	
	void craft(PlayerEntity player, Inventory inventory, ItemStack result);
}
