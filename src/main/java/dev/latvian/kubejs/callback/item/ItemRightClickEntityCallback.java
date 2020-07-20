package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public interface ItemRightClickEntityCallback {
	Event<ItemRightClickEntityCallback> EVENT = EventFactory.createArrayBacked(ItemRightClickEntityCallback.class, (listeners) -> {
		return (player, entity, hand, position) -> {
			for (ItemRightClickEntityCallback listener : listeners) {
				listener.interact(player, entity, hand, position);
			}
		};
	});

	void interact(PlayerEntity player, Entity entity, Hand hand, BlockPos position);
}
