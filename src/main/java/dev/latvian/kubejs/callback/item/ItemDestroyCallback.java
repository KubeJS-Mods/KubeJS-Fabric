package dev.latvian.kubejs.callback.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

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

	void destroy(PlayerEntity player, @Nonnull ItemStack original, @Nullable Hand hand);
}
