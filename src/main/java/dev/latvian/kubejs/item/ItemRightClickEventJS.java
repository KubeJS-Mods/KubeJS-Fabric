package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

/**
 * @author LatvianModder
 */
public class ItemRightClickEventJS extends PlayerEventJS {
	public final PlayerEntity player;
	public final Hand hand;
	public final ItemStack stack;
	public final BlockPos position;
	
	public ItemRightClickEventJS(PlayerEntity player, ItemStack stack, Hand hand, BlockPos position) {
		this.player = player;
		this.stack = stack;
		this.hand = hand;
		this.position = position;
	}
	
	@Override
	public boolean canCancel() {
		return true;
	}
	
	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}
	
	public Hand getHand() {
		return hand;
	}
	
	public ItemStackJS getItem() {
		return ItemStackJS.of(stack);
	}
}