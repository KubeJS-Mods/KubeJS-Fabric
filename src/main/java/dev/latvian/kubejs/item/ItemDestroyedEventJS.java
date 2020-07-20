package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ItemDestroyedEventJS extends PlayerEventJS
{
	public final PlayerEntity player;
	public final ItemStack original;
	public final Hand hand;

	public ItemDestroyedEventJS(PlayerEntity player, ItemStack original, Hand hand)
	{
		this.player = player;
		this.original = original;
		this.hand = hand;
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

	@Nullable
	public Hand getHand()
	{
		return hand;
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(original);
	}
}