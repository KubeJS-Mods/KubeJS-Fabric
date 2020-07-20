package dev.latvian.kubejs.core;

import dev.latvian.kubejs.server.ServerScriptManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ServerResourceManager;

import java.util.List;

/**
 * @author LatvianModder
 */
public interface DataPackRegistriesKJS
{
	default void initKJS()
	{
		try
		{
			ServerScriptManager.instance = new ServerScriptManager();
			SimpleReloadableResourceManagerKJS manager = (SimpleReloadableResourceManagerKJS) (((ServerResourceManager) this).getResourceManager());
			ResourceReloadListener reloadListener = ServerScriptManager.instance.createReloadListener();
			manager.getReloadListenersKJS().add(0, reloadListener);
			manager.getInitTaskQueueKJS().add(0, reloadListener);
		}
		catch (Exception ex)
		{
			throw new RuntimeException("KubeJS failed to register it's script loader!");
		}
	}

	static List<ResourcePack> getResourcePackListKJS(List<ResourcePack> list)
	{
		// ...
		return list;
	}
}
