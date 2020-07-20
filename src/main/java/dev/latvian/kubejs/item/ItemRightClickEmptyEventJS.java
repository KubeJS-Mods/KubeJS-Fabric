package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

/**
 * @author LatvianModder
 */
public class ItemRightClickEmptyEventJS extends PlayerEventJS
{
	public final PlayerEntity player;
	public final Hand hand;
	public final BlockPos position;

	public ItemRightClickEmptyEventJS(PlayerEntity player, Hand hand, BlockPos position)
	{
		this.player = player;
		this.hand = hand;
		this.position = position;
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
		return EmptyItemStackJS.INSTANCE;
	}
}