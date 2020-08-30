package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ItemDestroyedEventJS extends PlayerEventJS {
	public final Player player;
	public final ItemStack original;
	public final InteractionHand hand;
	
	public ItemDestroyedEventJS(Player player, ItemStack original, InteractionHand hand) {
		this.player = player;
		this.original = original;
		this.hand = hand;
	}
	
	@Override
	public boolean canCancel() {
		return true;
	}
	
	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}
	
	@Nullable
	public InteractionHand getHand() {
		return hand;
	}
	
	public ItemStackJS getItem() {
		return ItemStackJS.of(original);
	}
}