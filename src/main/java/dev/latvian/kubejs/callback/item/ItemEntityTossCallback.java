package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;

public interface ItemEntityTossCallback {
	Event<ItemEntityTossCallback> EVENT = EventFactory.createArrayBacked(ItemEntityTossCallback.class, (listeners) -> {
		return (player, entity) -> {
			for (ItemEntityTossCallback listener : listeners) {
				listener.toss(player, entity);
			}
		};
	});

	void toss(PlayerEntity player, ItemEntity entity);
}
