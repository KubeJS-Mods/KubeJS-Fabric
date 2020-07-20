package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.SimpleReloadableResourceManagerKJS;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
@Mixin(ReloadableResourceManagerImpl.class)
public abstract class SimpleReloadableResourceManagerMixin implements SimpleReloadableResourceManagerKJS
{
	@Override
	@Accessor("namespaceManagers")
	public abstract Map<String, NamespaceResourceManager> getNamespaceResourceManagersKJS();

	@Override
	@Accessor("listeners")
	public abstract List<ResourceReloadListener> getReloadListenersKJS();

	@Override
	@Accessor("initialListeners")
	public abstract List<ResourceReloadListener> getInitTaskQueueKJS();
}