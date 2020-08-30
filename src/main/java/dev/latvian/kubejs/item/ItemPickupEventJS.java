package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class ItemPickupEventJS extends PlayerEventJS {
	public final Player player;
	public final ItemEntity entity;
	
	public ItemPickupEventJS(Player player, ItemEntity entity) {
		this.player = player;
		this.entity = entity;
	}
	
	@Override
	public boolean canCancel() {
		return true;
	}
	
	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}
	
	public EntityJS getItemEntity() {
		return entityOf(entity);
	}
	
	public ItemStackJS getItem() {
		return ItemStackJS.of(entity.getItem());
	}
}