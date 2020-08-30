package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface ItemRightClickEntityCallback {
	Event<ItemRightClickEntityCallback> EVENT = EventFactory.createArrayBacked(ItemRightClickEntityCallback.class, (listeners) -> {
		return (player, entity, hand, position) -> {
			for (ItemRightClickEntityCallback listener : listeners) {
				listener.interact(player, entity, hand, position);
			}
		};
	});
	
	void interact(Player player, Entity entity, InteractionHand hand, BlockPos position);
}
