package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author LatvianModder
 */
public class ItemFoodEatenEventJS extends PlayerEventJS {
	private final ServerPlayerEntity player;
	public final ItemStackJS item;
	
	public ItemFoodEatenEventJS(ServerPlayerEntity p, ItemStack is) {
		player = p;
		item = ItemStackJS.of(is);
	}
	
	@Override
	public boolean canCancel() {
		return true;
	}
	
	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}
	
	public ItemStackJS getItem() {
		return item;
	}
}