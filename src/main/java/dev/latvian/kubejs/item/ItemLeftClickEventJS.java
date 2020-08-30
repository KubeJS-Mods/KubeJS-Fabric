package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class ItemLeftClickEventJS extends PlayerEventJS {
	public final Player player;
	public final InteractionHand hand;
	public final BlockPos position;
	
	public ItemLeftClickEventJS(Player player, InteractionHand hand, BlockPos position) {
		this.player = player;
		this.hand = hand;
		this.position = position;
	}
	
	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}
	
	public ItemStackJS getItem() {
		return ItemStackJS.of(player.getItemInHand(hand));
	}
}