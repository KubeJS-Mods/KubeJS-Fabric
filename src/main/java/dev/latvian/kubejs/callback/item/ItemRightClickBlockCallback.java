package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface ItemRightClickBlockCallback {
	Event<ItemRightClickBlockCallback> EVENT = EventFactory.createArrayBacked(ItemRightClickBlockCallback.class, (listeners) -> {
		return (player, hand, position, face) -> {
			for (ItemRightClickBlockCallback listener : listeners) {
				listener.rightClick(player, hand, position, face);
			}
		};
	});

	void rightClick(PlayerEntity player, Hand hand, BlockPos position, Direction face);
}
