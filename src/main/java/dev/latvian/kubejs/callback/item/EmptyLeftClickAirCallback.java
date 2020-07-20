package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public interface EmptyLeftClickAirCallback {
	Event<EmptyLeftClickAirCallback> EVENT = EventFactory.createArrayBacked(EmptyLeftClickAirCallback.class, (listeners) -> {
		return (player, hand, position) -> {
			for (EmptyLeftClickAirCallback listener : listeners) {
				listener.leftClickEmpty(player, hand, position);
			}
		};
	});
	
	void leftClickEmpty(PlayerEntity player, Hand hand, BlockPos position);
}
