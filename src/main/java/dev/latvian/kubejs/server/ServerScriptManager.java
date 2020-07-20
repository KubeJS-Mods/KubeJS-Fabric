package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.recipe.RecipeEventJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.script.*;
import dev.latvian.kubejs.script.data.DataPackEventJS;
import dev.latvian.kubejs.script.data.VirtualKubeJSDataPack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author LatvianModder
 */
public class ServerScriptManager {
	public static ServerScriptManager instance;
	
	public final ScriptManager scriptManager = new ScriptManager(ScriptType.SERVER);
	public final VirtualKubeJSDataPack virtualDataPackFirst = new VirtualKubeJSDataPack(true);
	public final VirtualKubeJSDataPack virtualDataPackLast = new VirtualKubeJSDataPack(false);
	
	public void reloadScripts(ReloadableResourceManagerImpl resourceManager) {
		scriptManager.unload();
		
		Map<String, List<Identifier>> packs = new HashMap<>();
		
		for (Identifier resource : resourceManager.findResources("kubejs", s -> s.endsWith(".js"))) {
			packs.computeIfAbsent(resource.getNamespace(), s -> new ArrayList<>()).add(resource);
		}
		
		for (Map.Entry<String, List<Identifier>> entry : packs.entrySet()) {
			ScriptPack pack = new ScriptPack(scriptManager, new ScriptPackInfo(entry.getKey(), "kubejs/"));
			
			for (Identifier id : entry.getValue()) {
				pack.info.scripts.add(new ScriptFileInfo(pack.info, id.getPath().substring(7)));
			}
			
			for (ScriptFileInfo fileInfo : pack.info.scripts) {
				ScriptSource scriptSource = info -> new InputStreamReader(resourceManager.getResource(info.location).getInputStream());
				Throwable error = fileInfo.preload(scriptSource);
				
				if (error == null) {
					if (fileInfo.shouldLoad(FabricLoader.getInstance().getEnvironmentType())) {
						pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
					}
				} else {
					KubeJS.LOGGER.error("Failed to pre-load script file " + fileInfo.location + ": " + error);
				}
			}
			
			pack.scripts.sort(null);
			scriptManager.packs.put(pack.info.namespace, pack);
		}
		
		//Loading is required in prepare stage to allow virtual data pack overrides
		virtualDataPackFirst.resetData();
		ScriptType.SERVER.console.setLineNumber(true);
		scriptManager.load();
		
		new DataPackEventJS(virtualDataPackFirst).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_FIRST);
		new DataPackEventJS(virtualDataPackLast).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_LAST);

		/*
		resourceManager.addResourcePack(virtualDataPackFirst);
		resourceManager.addResourcePack(virtualDataPackLast);

		Map<String, NamespaceResourceManager> namespaceResourceManagers = ((SimpleReloadableResourceManagerKJS) resourceManager).getNamespaceResourceManagersKJS();

		for (NamespaceResourceManager manager : namespaceResourceManagers.values())
		{
			if (manager.resourcePacks.remove(virtualDataPackLast))
			{
				manager.resourcePacks.add(0, virtualDataPackLast);
			}
		}
		 */
		
		ScriptType.SERVER.console.setLineNumber(false);
		ScriptType.SERVER.console.info("Scripts loaded");

		/*
		for (int i = 0; i < scriptManager.errors.size(); i++)
		{
			minecraftServer.getPlayerList().func_232641_a_(new LiteralText("#" + (i + 1) + ": ").func_240699_a_(Formatting.DARK_RED).func_230529_a_(new LiteralText(scriptManager.errors.get(i)).func_240699_a_(Formatting.RED)), ChatType.CHAT, Util.NIL_UUID);
		}
		 */
		
		Map<Identifier, RecipeTypeJS> typeMap = new HashMap<>();
		RegisterRecipeHandlersEvent.EVENT.invoker().accept(new RegisterRecipeHandlersEvent(typeMap));
		RecipeEventJS.instance = new RecipeEventJS(typeMap);
	}
	
	public ResourceReloadListener createReloadListener() {
		return (stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> {
			if (!(resourceManager instanceof ReloadableResourceManagerImpl)) {
				throw new RuntimeException("Resource manager is not ReloadableResourceManagerImpl, KubeJS will not work! Unsupported resource manager class: " + resourceManager.getClass());
			}
			
			reloadScripts((ReloadableResourceManagerImpl) resourceManager);
			return CompletableFuture.supplyAsync(Object::new, backgroundExecutor).thenCompose(stage::whenPrepared).thenAcceptAsync(o -> {}, gameExecutor);
		};
	}
}
