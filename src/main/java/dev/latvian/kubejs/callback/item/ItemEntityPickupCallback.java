package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;

public interface ItemEntityPickupCallback {
	Event<ItemEntityPickupCallback> EVENT = EventFactory.createArrayBacked(ItemEntityPickupCallback.class, (listeners) -> {
		return (player, entity) -> {
			for (ItemEntityPickupCallback listener : listeners) {
				listener.pickup(player, entity);
			}
		};
	});
	
	void pickup(PlayerEntity player, ItemEntity entity);
}
