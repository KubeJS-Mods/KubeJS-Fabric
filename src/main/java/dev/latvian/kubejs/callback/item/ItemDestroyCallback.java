package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ItemDestroyCallback {
	Event<ItemDestroyCallback> EVENT = EventFactory.createArrayBacked(ItemDestroyCallback.class, (listeners) -> {
		return (player, original, hand) -> {
			for (ItemDestroyCallback listener : listeners) {
				listener.destroy(player, original, hand);
			}
		};
	});
	
	void destroy(Player player, @Nonnull ItemStack original, @Nullable InteractionHand hand);
}
