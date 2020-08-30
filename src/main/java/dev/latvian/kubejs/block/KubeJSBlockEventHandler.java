package dev.latvian.kubejs.block;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.core.AfterScriptLoadCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * @author LatvianModder
 */
public class KubeJSBlockEventHandler implements KubeJSInitializer {
	@Override
	public void onKubeJSInitialization() {
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
	
	private InteractionResult rightClick(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
		if (new BlockRightClickEventJS(player, world, hand, hitResult).post(KubeJSEvents.BLOCK_RIGHT_CLICK)) {
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
	
	private InteractionResult leftClick(Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
		if (new BlockLeftClickEventJS(player, world, hand, pos, direction).post(KubeJSEvents.BLOCK_LEFT_CLICK)) {
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
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