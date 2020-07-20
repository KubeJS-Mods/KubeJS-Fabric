package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJSEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

/**
 * @author LatvianModder
 */
public class InventoryListener implements ScreenHandlerListener
{
	public final ServerPlayerEntity player;

	public InventoryListener(ServerPlayerEntity p)
	{
		player = p;
	}

	@Override
	public void onHandlerRegistered(ScreenHandler container, DefaultedList<ItemStack> itemsList)
	{
		new InventoryChangedEventJS(player, ItemStack.EMPTY, -1).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
	}

	@Override
	public void onSlotUpdate(ScreenHandler container, int index, ItemStack stack)
	{
		if (!stack.isEmpty() && container.getSlot(index).inventory == player.inventory)
		{
			new InventoryChangedEventJS(player, stack, index).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
		}
	}

	@Override
	public void onPropertyUpdate(ScreenHandler container, int id, int value)
	{
	}
}