package dev.latvian.kubejs.item;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.callback.item.*;
import dev.latvian.kubejs.core.AfterScriptLoadCallback;
import dev.latvian.kubejs.fluid.BucketItemJS;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.player.InventoryChangedEventJS;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class KubeJSItemEventHandler implements KubeJSInitializer {
	@Override
	public void onKubeJSInitialization() {
		AfterScriptLoadCallback.EVENT.register(this::registry);
		ItemRightClickAirCallback.EVENT.register(this::rightClick); // Done
//		EmptyRightClickAirCallback.EVENT.register(this::rightClickEmpty); // Done
//		EmptyLeftClickAirCallback.EVENT.register(this::leftClickEmpty); // Done
		ItemEntityPickupCallback.EVENT.register(this::pickup); // Done
		ItemEntityTossCallback.EVENT.register(this::toss); // Done
		ItemRightClickEntityCallback.EVENT.register(this::entityInteract); // Done
		ItemCraftCallback.EVENT.register(this::crafted); // Done
//		ItemSmeltCallback.EVENT.register(this::smelted); // Done
		ItemDestroyCallback.EVENT.register(this::destroyed); // Done
	}
	
	private void registry() {
		for (ItemBuilder builder : KubeJSObjects.ITEMS.values()) {
			builder.item = new ItemJS(builder);
			Registry.register(Registry.ITEM, builder.id, builder.item);
		}
		
		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values()) {
			if (builder.itemBuilder != null) {
				builder.itemBuilder.blockItem = new BlockItemJS(builder.itemBuilder);
				Registry.register(Registry.ITEM, builder.id, builder.itemBuilder.blockItem);
			}
		}
		
		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values()) {
			builder.bucketItem = new BucketItemJS(builder);
			Registry.register(Registry.ITEM, builder.id.getNamespace() + ":" + builder.id.getPath() + "_bucket", builder.bucketItem);
		}
	}
	
	private InteractionResult rightClick(Player player, ItemStack stack, InteractionHand hand, BlockPos position) {
		if (new ItemRightClickEventJS(player, stack, hand, position).post(KubeJSEvents.ITEM_RIGHT_CLICK)) {
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	private void rightClickEmpty(Player player, InteractionHand hand, BlockPos position) {
		new ItemRightClickEmptyEventJS(player, hand, position).post(KubeJSEvents.ITEM_RIGHT_CLICK_EMPTY);
	}
	
	private void leftClickEmpty(Player player, InteractionHand hand, BlockPos position) {
		new ItemLeftClickEventJS(player, hand, position).post(KubeJSEvents.ITEM_LEFT_CLICK);
	}
	
	private InteractionResult pickup(Player player, ItemEntity entity) {
		if (player != null && player.level != null && new ItemPickupEventJS(player, entity).post(KubeJSEvents.ITEM_PICKUP)) {
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	private InteractionResult toss(Player player, ItemEntity entity) {
		if (player != null && player.level != null && new ItemTossEventJS(player, entity).post(KubeJSEvents.ITEM_TOSS)) {
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	private InteractionResult entityInteract(Player player, Entity entity, InteractionHand hand, BlockPos position) {
		if (new ItemEntityInteractEventJS(player, entity, hand, position).post(KubeJSEvents.ITEM_ENTITY_INTERACT)) {
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.FAIL;
	}
	
	private void crafted(Player player, Container inventory, ItemStack result) {
		if (player instanceof ServerPlayer && !result.isEmpty()) {
			new ItemCraftedEventJS(player, inventory, result).post(KubeJSEvents.ITEM_CRAFTED);
			new InventoryChangedEventJS((ServerPlayer) player, result, -1).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
		}
	}
	
	private void smelted(Container inventory, ItemStack result) {
		if (!result.isEmpty()) {
			new ItemSmeltedEventJS(inventory, result).post(ScriptType.SERVER, KubeJSEvents.ITEM_SMELTED);
		}
	}
	
	private void destroyed(Player player, @Nonnull ItemStack original, @Nullable InteractionHand hand) {
		if (player instanceof ServerPlayer) {
			new ItemDestroyedEventJS(player, original, hand).post(KubeJSEvents.ITEM_DESTROYED);
		}
	}
}