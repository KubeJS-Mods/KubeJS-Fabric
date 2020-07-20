package dev.latvian.kubejs.block;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BlockLeftClickEventJS extends PlayerEventJS
{
	public final PlayerEntity player;
	public final World world;
	public final BlockPos pos;
	public final Hand hand;
	public final Direction direction;

	public BlockLeftClickEventJS(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction)
	{
		this.player = player;
		this.world = world;
		this.pos = pos;
		this.hand = hand;
		this.direction = direction;
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

	public BlockContainerJS getBlock()
	{
		return new BlockContainerJS(world, pos);
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(player.getStackInHand(hand));
	}

	@Nullable
	public Direction getFacing()
	{
		return direction;
	}
}