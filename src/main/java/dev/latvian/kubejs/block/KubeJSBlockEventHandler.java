package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.core.AfterScriptLoadCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class KubeJSBlockEventHandler {
	public void init() {
		AfterScriptLoadCallback.EVENT.register(this::registry);
		UseBlockCallback.EVENT.register(this::rightClick);
		AttackBlockCallback.EVENT.register(this::leftClick);
//		MinecraftForge.EVENT_BUS.addListener(this::blockBreak);
//		MinecraftForge.EVENT_BUS.addListener(this::blockPlace);
//		MinecraftForge.EVENT_BUS.addListener(this::blockDrops);
	}
	
	private void registry() {
		for (BlockBuilder builder : KubeJSObjects.BLOCKS.values()) {
			BlockBuilder.current = builder;
			builder.block = new BlockJS(builder);
			Registry.register(Registry.BLOCK, builder.id, builder.block);
		}
		
		BlockBuilder.current = null;
		
		// TODO Fix fluids
//		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values())
//		{
//			builder.block = new FlowingFluidBlock(() -> builder.stillFluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
//			builder.block.setRegistryName(builder.id);
//			event.getRegistry().register(builder.block);
//		}
	}
	
	private ActionResult rightClick(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		if (new BlockRightClickEventJS(player, world, hand, hitResult).post(KubeJSEvents.BLOCK_RIGHT_CLICK)) {
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}
	
	private ActionResult leftClick(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
		if (new BlockLeftClickEventJS(player, world, hand, pos, direction).post(KubeJSEvents.BLOCK_LEFT_CLICK)) {
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	/* TODO
	private void blockBreak(BlockEvent.BreakEvent event)
	{
		if (new BlockBreakEventJS(event).post(KubeJSEvents.BLOCK_BREAK))
		{
			event.setCanceled(true);
		}
	}

// TODO
	private void blockPlace(BlockEvent.EntityPlaceEvent event)
	{
		if (new BlockPlaceEventJS(event).post(KubeJSEvents.BLOCK_PLACE))
		{
			event.setCanceled(true);
		}
	}

// TODO
	private void blockDrops(BlockEvent.HarvestDropsEvent event)
	{
		if (event.getWorld().isClient())
		{
			return;
		}

		BlockDropsEventJS e = new BlockDropsEventJS(event);
		e.post(KubeJSEvents.BLOCK_DROPS);

		if (e.dropList != null)
		{
			event.getDrops().clear();

			for (ItemStackJS stack : e.dropList)
			{
				event.getDrops().add(stack.getItemStack());
			}
		}
	}*/
}