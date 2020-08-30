package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ItemRightClickAirCallback {
	Event<ItemRightClickAirCallback> EVENT = EventFactory.createArrayBacked(ItemRightClickAirCallback.class, (listeners) -> {
		return (player, stack, hand, position) -> {
			for (ItemRightClickAirCallback listener : listeners) {
				listener.rightClick(player, stack, hand, position);
			}
		};
	});
	
	void rightClick(Player player, ItemStack stack, InteractionHand hand, BlockPos position);
}
