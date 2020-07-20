package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public interface ItemRightClickAirCallback {
	Event<ItemRightClickAirCallback> EVENT = EventFactory.createArrayBacked(ItemRightClickAirCallback.class, (listeners) -> {
		return (player, stack, hand, position) -> {
			for (ItemRightClickAirCallback listener : listeners) {
				listener.rightClick(player, stack, hand, position);
			}
		};
	});

	void rightClick(PlayerEntity player, ItemStack stack, Hand hand, BlockPos position);
}
