package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public interface EmptyRightClickAirCallback {
	Event<EmptyRightClickAirCallback> EVENT = EventFactory.createArrayBacked(EmptyRightClickAirCallback.class, (listeners) -> {
		return (player, hand, position) -> {
			for (EmptyRightClickAirCallback listener : listeners) {
				listener.rightClickEmpty(player, hand, position);
			}
		};
	});
	
	void rightClickEmpty(Player player, InteractionHand hand, BlockPos position);
}
