package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @author LatvianModder
 */
public class ItemPickupEventJS extends PlayerEventJS
{
	public final PlayerEntity player;
	public final ItemEntity entity;

	public ItemPickupEventJS(PlayerEntity player, ItemEntity entity)
	{
		this.player = player;
		this.entity = entity;
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(player);
	}

	public EntityJS getItemEntity()
	{
		return entityOf(entity);
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(entity.getStack());
	}
}