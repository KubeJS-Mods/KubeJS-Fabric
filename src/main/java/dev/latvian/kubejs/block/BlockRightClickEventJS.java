package dev.latvian.kubejs.block;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * @author LatvianModder
 */
public class BlockRightClickEventJS extends PlayerEventJS {
	public final Player player;
	public final Level world;
	public final InteractionHand hand;
	public final BlockHitResult blockHitResult;
	private BlockContainerJS block;
	private ItemStackJS item;
	
	public BlockRightClickEventJS(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
		this.player = player;
		this.world = world;
		this.hand = hand;
		this.blockHitResult = hitResult;
	}
	
	@Override
	public boolean canCancel() {
		return true;
	}
	
	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}
	
	public BlockContainerJS getBlock() {
		if (block == null) {
			block = new BlockContainerJS(world, blockHitResult.getBlockPos());
		}
		
		return block;
	}
	
	public InteractionHand getHand() {
		return hand;
	}
	
	public ItemStackJS getItem() {
		if (item == null) {
			item = ItemStackJS.of(player.getItemInHand(hand));
		}
		
		return item;
	}
	
	public Direction getFacing() {
		return blockHitResult.getDirection();
	}
}