package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface ItemSmeltCallback {
	Event<ItemSmeltCallback> EVENT = EventFactory.createArrayBacked(ItemSmeltCallback.class, (listeners) -> {
		return (inventory, result) -> {
			for (ItemSmeltCallback listener : listeners) {
				listener.smelt(inventory, result);
			}
		};
	});
	
	void smelt(Inventory inventory, ItemStack result);
}
