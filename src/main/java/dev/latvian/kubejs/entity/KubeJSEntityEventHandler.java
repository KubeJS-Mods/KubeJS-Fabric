package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.KubeJSInitializer;

/**
 * @author LatvianModder
 */
public class KubeJSEntityEventHandler implements KubeJSInitializer {
	@Override
	public void onKubeJSInitialization() {
//		MinecraftForge.EVENT_BUS.addListener(this::livingDeath);
//		MinecraftForge.EVENT_BUS.addListener(this::livingAttack);
//		MinecraftForge.EVENT_BUS.addListener(this::livingDrops);
//		MinecraftForge.EVENT_BUS.addListener(this::checkLivingSpawn);
//		MinecraftForge.EVENT_BUS.addListener(this::entitySpawned);
	}
	
	// TODO
//	private void livingDeath(LivingDeathEvent event)
//	{
//		if (new LivingEntityDeathEventJS(event).post(KubeJSEvents.ENTITY_DEATH))
//		{
//			event.setCanceled(true);
//		}
//	}
//
//	private void livingAttack(LivingAttackEvent event)
//	{
//		if (event.getAmount() > 0F && new LivingEntityAttackEventJS(event).post(KubeJSEvents.ENTITY_ATTACK))
//		{
//			event.setCanceled(true);
//		}
//	}
//
//	private void livingDrops(LivingDropsEvent event)
//	{
//		if (event.getEntity().world.isClient())
//		{
//			return;
//		}
//
//		LivingEntityDropsEventJS e = new LivingEntityDropsEventJS(event);
//
//		if (e.post(KubeJSEvents.ENTITY_DROPS))
//		{
//			event.setCanceled(true);
//		}
//		else if (e.drops != null)
//		{
//			event.getDrops().clear();
//
//			for (ItemEntityJS ie : e.drops)
//			{
//				event.getDrops().add((ItemEntity) ie.minecraftEntity);
//			}
//		}
//	}
//
//	private void checkLivingSpawn(LivingSpawnEvent.CheckSpawn event)
//	{
//		if (ServerJS.instance != null && ServerJS.instance.overworld != null && !event.getWorld().isClient() && new CheckLivingEntitySpawnEventJS(event).post(ScriptType.SERVER, KubeJSEvents.ENTITY_CHECK_SPAWN))
//		{
//			event.setResult(Event.Result.DENY);
//		}
//	}
//
//	private void entitySpawned(EntityJoinWorldEvent event)
//	{
//		if (ServerJS.instance != null && ServerJS.instance.overworld != null && !event.getWorld().isClient() && new EntitySpawnedEventJS(event).post(ScriptType.SERVER, KubeJSEvents.ENTITY_SPAWNED))
//		{
//			event.setCanceled(true);
//		}
//	}
}