package dev.latvian.kubejs.block;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class BlockRightClickEventJS extends PlayerEventJS
{
	public final PlayerEntity player;
	public final World world;
	public final Hand hand;
	public final BlockHitResult blockHitResult;
	private BlockContainerJS block;
	private ItemStackJS item;

	public BlockRightClickEventJS(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult)
	{
		this.player = player;
		this.world = world;
		this.hand = hand;
		this.blockHitResult = hitResult;
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
		if (block == null)
		{
			block = new BlockContainerJS(world, blockHitResult.getBlockPos());
		}

		return block;
	}

	public Hand getHand()
	{
		return hand;
	}

	public ItemStackJS getItem()
	{
		if (item == null)
		{
			item = ItemStackJS.of(player.getStackInHand(hand));
		}

		return item;
	}

	public Direction getFacing()
	{
		return blockHitResult.getSide();
	}
}