package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class ItemEntityInteractEventJS extends PlayerEventJS {
	public final Player player;
	public final Entity entity;
	public final InteractionHand hand;
	public final BlockPos position;
	
	public ItemEntityInteractEventJS(Player player, Entity entity, InteractionHand hand, BlockPos position) {
		this.player = player;
		this.entity = entity;
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
		return ItemStackJS.of(player.getItemInHand(hand));
	}
	
	public EntityJS getTarget() {
		return getWorld().getEntity(entity);
	}
}