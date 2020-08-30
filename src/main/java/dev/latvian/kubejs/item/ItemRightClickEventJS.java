package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemRightClickEventJS extends PlayerEventJS {
	public final Player player;
	public final InteractionHand hand;
	public final ItemStack stack;
	public final BlockPos position;
	
	public ItemRightClickEventJS(Player player, ItemStack stack, InteractionHand hand, BlockPos position) {
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
	
	public InteractionHand getHand() {
		return hand;
	}
	
	public ItemStackJS getItem() {
		return ItemStackJS.of(stack);
	}
}