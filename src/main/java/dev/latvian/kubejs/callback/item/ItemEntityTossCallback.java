package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

public interface ItemEntityTossCallback {
	Event<ItemEntityTossCallback> EVENT = EventFactory.createArrayBacked(ItemEntityTossCallback.class, (listeners) -> {
		return (player, entity) -> {
			for (ItemEntityTossCallback listener : listeners) {
				listener.toss(player, entity);
			}
		};
	});
	
	void toss(Player player, ItemEntity entity);
}
