package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public interface EmptyLeftClickAirCallback {
	Event<EmptyLeftClickAirCallback> EVENT = EventFactory.createArrayBacked(EmptyLeftClickAirCallback.class, (listeners) -> {
		return (player, hand, position) -> {
			for (EmptyLeftClickAirCallback listener : listeners) {
				listener.leftClickEmpty(player, hand, position);
			}
		};
	});
	
	void leftClickEmpty(Player player, InteractionHand hand, BlockPos position);
}
