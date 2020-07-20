package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public interface EmptyRightClickAirCallback {
	Event<EmptyRightClickAirCallback> EVENT = EventFactory.createArrayBacked(EmptyRightClickAirCallback.class, (listeners) -> {
		return (player, hand, position) -> {
			for (EmptyRightClickAirCallback listener : listeners) {
				listener.rightClickEmpty(player, hand, position);
			}
		};
	});
	
	void rightClickEmpty(PlayerEntity player, Hand hand, BlockPos position);
}
