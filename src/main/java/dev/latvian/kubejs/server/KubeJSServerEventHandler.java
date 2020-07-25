package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.command.CommandRegistryEventJS;
import dev.latvian.kubejs.command.KubeJSCommands;
import dev.latvian.kubejs.core.ResourcePackManagerKJS;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.player.SimplePlayerEventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.data.KubeJSDataPackFinder;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.SimpleWorldEventJS;
import dev.latvian.kubejs.world.WorldJS;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author LatvianModder
 */
public class KubeJSServerEventHandler implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(this::serverStarting);
		ServerLifecycleEvents.SERVER_STOPPING.register(this::serverStopping);
		ServerTickEvents.START_SERVER_TICK.register(this::serverTick);
		CommandRegistrationCallback.EVENT.register(KubeJSCommands::register);
	}
	
	public void serverStarting(MinecraftServer server) {
		if (ServerJS.instance != null) {
			destroyServer();
		}
		
		ServerJS.instance = new ServerJS(server, ServerScriptManager.instance);
		new CommandRegistryEventJS(server.isSinglePlayer(), server.getCommandManager().getDispatcher()).post(ScriptType.SERVER, KubeJSEvents.COMMAND_REGISTRY);
		
		ServerJS.instance.overworld = new ServerWorldJS(ServerJS.instance, ServerJS.instance.minecraftServer.getWorld(World.OVERWORLD));
		ServerJS.instance.worldMap.put("minecraft:overworld", ServerJS.instance.overworld);
		ServerJS.instance.worlds.add(ServerJS.instance.overworld);
		
		for (ServerWorld world : ServerJS.instance.minecraftServer.getWorlds()) {
			if (world != ServerJS.instance.overworld.minecraftWorld) {
				ServerWorldJS w = new ServerWorldJS(ServerJS.instance, world);
				ServerJS.instance.worldMap.put(world.getDimensionRegistryKey().getValue().toString(), w);
			}
		}
		
		ServerJS.instance.updateWorldList();
		
		AttachServerDataEvent.EVENT.invoker().accept(new AttachServerDataEvent(ServerJS.instance));
		new ServerEventJS().post(ScriptType.SERVER, KubeJSEvents.SERVER_LOAD);
		
		for (ServerWorldJS world : ServerJS.instance.worlds) {
			AttachWorldDataEvent.EVENT.invoker().accept(new AttachWorldDataEvent(ServerJS.instance.getOverworld()));
			new SimpleWorldEventJS(ServerJS.instance.getOverworld()).post(KubeJSEvents.WORLD_LOAD);
		}
		
		((ResourcePackManagerKJS) server.getDataPackManager()).addProviderKJS(new KubeJSDataPackFinder(KubeJS.getGameDirectory().resolve("kubejs").toFile()));
	}
	
	public void serverStopping(MinecraftServer server) {
		destroyServer();
	}
	
	public static void destroyServer() {
		for (PlayerDataJS p : new ArrayList<>(ServerJS.instance.playerMap.values())) {
			new SimplePlayerEventJS(p.getMinecraftPlayer()).post(KubeJSEvents.PLAYER_LOGGED_OUT);
			ServerJS.instance.playerMap.remove(p.getId());
		}
		
		ServerJS.instance.playerMap.clear();
		
		for (WorldJS w : new ArrayList<>(ServerJS.instance.worldMap.values())) {
			new SimpleWorldEventJS(w).post(KubeJSEvents.WORLD_UNLOAD);
			ServerJS.instance.worldMap.remove(w.getDimension());
		}
		
		ServerJS.instance.updateWorldList();
		
		new ServerEventJS().post(ScriptType.SERVER, KubeJSEvents.SERVER_UNLOAD);
		ServerJS.instance = null;
	}

	public void serverTick(MinecraftServer event)
	{
		ServerJS s = ServerJS.instance;

		if (!s.scheduledEvents.isEmpty())
		{
			long now = System.currentTimeMillis();
			Iterator<ScheduledEvent> eventIterator = s.scheduledEvents.iterator();
			List<ScheduledEvent> list = new ArrayList<>();

			while (eventIterator.hasNext())
			{
				ScheduledEvent e = eventIterator.next();

				if (now >= e.getEndTime())
				{
					list.add(e);
					eventIterator.remove();
				}
			}

			for (ScheduledEvent e : list)
			{
				try
				{
					e.call();
				}
				catch (Throwable ex)
				{
					if (ex.getClass().getName().equals("jdk.nashorn.api.scripting.NashornException"))
					{
						e.file.pack.manager.type.console.error("Error occurred while handling scheduled event callback in " + e.file.info.location + ": " + ex);
					}
					else
					{
						ex.printStackTrace();
					}
				}
			}
		}

		if (!s.scheduledTickEvents.isEmpty())
		{
			long now = s.getOverworld().getTime();
			Iterator<ScheduledEvent> eventIterator = s.scheduledTickEvents.iterator();
			List<ScheduledEvent> list = new ArrayList<>();

			while (eventIterator.hasNext())
			{
				ScheduledEvent e = eventIterator.next();

				if (now >= e.getEndTime())
				{
					list.add(e);
					eventIterator.remove();
				}
			}

			for (ScheduledEvent e : list)
			{
				try
				{
					e.call();
				}
				catch (Throwable ex)
				{
					if (ex.getClass().getName().equals("jdk.nashorn.api.scripting.NashornException"))
					{
						e.file.pack.manager.type.console.error("Error occurred while handling scheduled event callback in " + e.file.info.location + ": " + ex);
					}
					else
					{
						ex.printStackTrace();
					}
				}
			}
		}

		new ServerEventJS().post(ScriptType.SERVER, KubeJSEvents.SERVER_TICK);
	}
//
//	@SubscribeEvent(priority = EventPriority.LOW)
//	public static void command(CommandEvent event)
//	{
//		if (new CommandEventJS(event).post(ScriptType.SERVER, KubeJSEvents.COMMAND_RUN))
//		{
//			event.setCanceled(true);
//		}
//	}
}