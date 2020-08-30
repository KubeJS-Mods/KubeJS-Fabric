package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ItemCraftCallback {
	Event<ItemCraftCallback> EVENT = EventFactory.createArrayBacked(ItemCraftCallback.class, (listeners) -> {
		return (player, inventory, result) -> {
			for (ItemCraftCallback listener : listeners) {
				listener.craft(player, inventory, result);
			}
		};
	});
	
	void craft(Player player, Container inventory, ItemStack result);
}
