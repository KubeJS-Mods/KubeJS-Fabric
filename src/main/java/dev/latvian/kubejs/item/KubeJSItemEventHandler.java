package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.callback.item.*;
import dev.latvian.kubejs.core.AfterScriptLoadCallback;
import dev.latvian.kubejs.fluid.BucketItemJS;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.player.InventoryChangedEventJS;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class KubeJSItemEventHandler
{
	public void init()
	{
		AfterScriptLoadCallback.EVENT.register(this::registry);
		ItemRightClickAirCallback.EVENT.register(this::rightClick); // Done
		EmptyRightClickAirCallback.EVENT.register(this::rightClickEmpty); // Done
		EmptyLeftClickAirCallback.EVENT.register(this::leftClickEmpty); // Done
		ItemEntityPickupCallback.EVENT.register(this::pickup); // Done
		ItemEntityTossCallback.EVENT.register(this::toss); // Done
		ItemRightClickEntityCallback.EVENT.register(this::entityInteract); // Done
		ItemCraftCallback.EVENT.register(this::crafted); // Done
		ItemSmeltCallback.EVENT.register(this::smelted); // Done
		ItemDestroyCallback.EVENT.register(this::destroyed); // Done
	}

	private void registry()
	{
		for (ItemBuilder builder : KubeJSObjects.ITEMS.values())
		{
			builder.item = new ItemJS(builder);
			Registry.register(Registry.ITEM, builder.id, builder.item);
		}

		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values())
		{
			if (builder.itemBuilder != null)
			{
				builder.itemBuilder.blockItem = new BlockItemJS(builder.itemBuilder);
				Registry.register(Registry.ITEM, builder.id, builder.itemBuilder.blockItem);
			}
		}

		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values())
		{
			builder.bucketItem = new BucketItemJS(builder);
			Registry.register(Registry.ITEM, builder.id.getNamespace() + ":" + builder.id.getPath() + "_bucket", builder.bucketItem);
		}
	}

	private ActionResult rightClick(PlayerEntity player, ItemStack stack, Hand hand, BlockPos position)
	{
		if (new ItemRightClickEventJS(player, stack, hand, position).post(KubeJSEvents.ITEM_RIGHT_CLICK))
		{
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	private void rightClickEmpty(PlayerEntity player, Hand hand, BlockPos position)
	{
		new ItemRightClickEmptyEventJS(player, hand, position).post(KubeJSEvents.ITEM_RIGHT_CLICK_EMPTY);
	}

	private void leftClickEmpty(PlayerEntity player, Hand hand, BlockPos position)
	{
		new ItemLeftClickEventJS(player, hand, position).post(KubeJSEvents.ITEM_LEFT_CLICK);
	}

	private ActionResult pickup(PlayerEntity player, ItemEntity entity)
	{
		if (player != null && player.world != null && new ItemPickupEventJS(player, entity).post(KubeJSEvents.ITEM_PICKUP))
		{
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	private ActionResult toss(PlayerEntity player, ItemEntity entity)
	{
		if (player != null && player.world != null && new ItemTossEventJS(player, entity).post(KubeJSEvents.ITEM_TOSS))
		{
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	private ActionResult entityInteract(PlayerEntity player, Entity entity, Hand hand, BlockPos position)
	{
		if (new ItemEntityInteractEventJS(player, entity, hand, position).post(KubeJSEvents.ITEM_ENTITY_INTERACT))
		{
			return ActionResult.SUCCESS;
		}

		return ActionResult.FAIL;
	}

	private void crafted(PlayerEntity player, Inventory inventory, ItemStack result)
	{
		if (player instanceof ServerPlayerEntity && !result.isEmpty())
		{
			new ItemCraftedEventJS(player, inventory, result).post(KubeJSEvents.ITEM_CRAFTED);
			new InventoryChangedEventJS((ServerPlayerEntity) player, result, -1).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
		}
	}

	private void smelted(Inventory inventory, ItemStack result)
	{
		if (!result.isEmpty())
		{
			new ItemSmeltedEventJS(inventory, result).post(ScriptType.SERVER, KubeJSEvents.ITEM_SMELTED);
		}
	}

	private void destroyed(PlayerEntity player, @Nonnull ItemStack original, @Nullable Hand hand)
	{
		if (player instanceof ServerPlayerEntity)
		{
			new ItemDestroyedEventJS(player, original, hand).post(KubeJSEvents.ITEM_DESTROYED);
		}
	}
}