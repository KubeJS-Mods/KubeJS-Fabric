package dev.latvian.kubejs.world;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;

/**
 * @author LatvianModder
 */
public class KubeJSWorldEventHandler
{
	public void init()
	{
		// TODO
//		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::worldLoaded);
//		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::worldUnloaded);
		ServerTickEvents.END_WORLD_TICK.register(this::worldTick);
//		MinecraftForge.EVENT_BUS.addListener(this::explosionStart);
//		MinecraftForge.EVENT_BUS.addListener(this::explosionDetonate);
//		MinecraftForge.EVENT_BUS.addGenericListener(Block.class, this::missingBlockMappings);
//		MinecraftForge.EVENT_BUS.addGenericListener(Item.class, this::missingItemMappings);
	}

//	private void worldLoaded(WorldEvent.Load event)
//	{
//		if (ServerJS.instance != null && ServerJS.instance.overworld != null && event.getWorld() instanceof ServerWorld && !ServerJS.instance.worldMap.containsKey(((ServerWorld) event.getWorld()).func_234922_V_().func_240901_a_().toString()))
//		{
//			ServerWorldJS w = new ServerWorldJS(ServerJS.instance, (ServerWorld) event.getWorld());
//			ServerJS.instance.worldMap.put(((ServerWorld) event.getWorld()).func_234922_V_().func_240901_a_().toString(), w);
//			ServerJS.instance.updateWorldList();
//			MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(w));
//			new SimpleWorldEventJS(w).post(ScriptType.SERVER, KubeJSEvents.WORLD_LOAD);
//		}
//	}
//
//	private void worldUnloaded(WorldEvent.Unload event)
//	{
//		if (ServerJS.instance != null && ServerJS.instance.overworld != null && event.getWorld() instanceof ServerWorld && ServerJS.instance.worldMap.containsKey(((ServerWorld) event.getWorld()).func_234922_V_().func_240901_a_().toString()))
//		{
//			WorldJS w = ServerJS.instance.getWorld(((ServerWorld) event.getWorld()).func_234922_V_().func_240901_a_().toString());
//			new SimpleWorldEventJS(w).post(ScriptType.SERVER, KubeJSEvents.WORLD_UNLOAD);
//			ServerJS.instance.worldMap.remove(w.getDimension());
//			ServerJS.instance.updateWorldList();
//		}
//	}

	private void worldTick(ServerWorld world)
	{
		if (!world.isClient)
		{
			WorldJS w = ServerJS.instance.getWorld(world);
			new SimpleWorldEventJS(w).post(ScriptType.SERVER, KubeJSEvents.WORLD_TICK);
		}
	}

//	private void explosionStart(ExplosionEvent.Start event)
//	{
//		if (new ExplosionEventJS.Pre(event).post(KubeJSEvents.WORLD_EXPLOSION_PRE))
//		{
//			event.setCanceled(true);
//		}
//	}
//
//	private void explosionDetonate(ExplosionEvent.Detonate event)
//	{
//		new ExplosionEventJS.Post(event).post(KubeJSEvents.WORLD_EXPLOSION_POST);
//	}
//
//	private void missingBlockMappings(RegistryEvent.MissingMappings<Block> event)
//	{
//		new MissingMappingEventJS<>(event, Registry.BLOCKS::getValue).post(ScriptType.STARTUP, KubeJSEvents.BLOCK_MISSING_MAPPINGS);
//	}
//
//	private void missingItemMappings(RegistryEvent.MissingMappings<Item> event)
//	{
//		new MissingMappingEventJS<>(event, Registry.ITEMS::getValue).post(ScriptType.STARTUP, KubeJSEvents.ITEM_MISSING_MAPPINGS);
//	}
}