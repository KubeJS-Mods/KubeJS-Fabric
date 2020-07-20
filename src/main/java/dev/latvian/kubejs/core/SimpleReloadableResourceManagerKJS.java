package dev.latvian.kubejs.core;

import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ResourceReloadListener;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public interface SimpleReloadableResourceManagerKJS {
	Map<String, NamespaceResourceManager> getNamespaceResourceManagersKJS();
	
	List<ResourceReloadListener> getReloadListenersKJS();
	
	List<ResourceReloadListener> getInitTaskQueueKJS();
}