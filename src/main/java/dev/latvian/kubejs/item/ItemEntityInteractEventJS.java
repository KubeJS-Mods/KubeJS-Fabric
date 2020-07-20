package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

/**
 * @author LatvianModder
 */
public class ItemEntityInteractEventJS extends PlayerEventJS
{
	public final PlayerEntity player;
	public final Entity entity;
	public final Hand hand;
	public final BlockPos position;

	public ItemEntityInteractEventJS(PlayerEntity player, Entity entity, Hand hand, BlockPos position)
	{
		this.player = player;
		this.entity = entity;
		this.hand = hand;
		this.position = position;
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

	public Hand getHand()
	{
		return hand;
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(player.getStackInHand(hand));
	}

	public EntityJS getTarget()
	{
		return getWorld().getEntity(entity);
	}
}