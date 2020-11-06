package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.recipe.RecipeEventJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.script.*;
import dev.latvian.kubejs.script.data.DataPackEventJS;
import dev.latvian.kubejs.script.data.VirtualKubeJSDataPack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;

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
	
	public final ScriptManager scriptManager = new ScriptManager(ScriptType.SERVER, KubeJSPaths.SERVER_SCRIPTS, "/data/kubejs/example_server_script.js");
	public final VirtualKubeJSDataPack virtualDataPackFirst = new VirtualKubeJSDataPack(true);
	public final VirtualKubeJSDataPack virtualDataPackLast = new VirtualKubeJSDataPack(false);
	
	public void reloadScripts(SimpleReloadableResourceManager resourceManager) {
		scriptManager.unload();
		
		Map<String, List<ResourceLocation>> packs = new HashMap<>();
		
		for (ResourceLocation resource : resourceManager.listResources("kubejs", s -> s.endsWith(".js"))) {
			packs.computeIfAbsent(resource.getNamespace(), s -> new ArrayList<>()).add(resource);
		}
		
		for (Map.Entry<String, List<ResourceLocation>> entry : packs.entrySet()) {
			ScriptPack pack = new ScriptPack(scriptManager, new ScriptPackInfo(entry.getKey(), "kubejs/"));
			
			for (ResourceLocation id : entry.getValue()) {
				pack.info.scripts.add(new ScriptFileInfo(pack.info, id.getPath().substring(7)));
			}
			
			for (ScriptFileInfo fileInfo : pack.info.scripts) {
				ScriptSource.FromResource scriptSource = info -> resourceManager.getResource(info.location);
				Throwable error = fileInfo.preload(scriptSource);
				
				if (error == null) {
					pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
				} else {
					KubeJS.LOGGER.error("Failed to pre-load script file " + fileInfo.location + ": " + error);
				}
			}
			
			pack.scripts.sort(null);
			scriptManager.packs.put(pack.info.namespace, pack);
		}

		scriptManager.loadFromDirectory();
		
		//Loading is required in prepare stage to allow virtual data pack overrides
		virtualDataPackFirst.resetData();
		ScriptType.SERVER.console.setLineNumber(true);
		scriptManager.load();
		
		new DataPackEventJS(virtualDataPackFirst).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_FIRST);
		new DataPackEventJS(virtualDataPackLast).post(ScriptType.SERVER, KubeJSEvents.SERVER_DATAPACK_LAST);
		
		ScriptType.SERVER.console.setLineNumber(false);
		ScriptType.SERVER.console.info("Scripts loaded");
		
		Map<ResourceLocation, RecipeTypeJS> typeMap = new HashMap<>();
		RegisterRecipeHandlersEvent.EVENT.invoker().accept(new RegisterRecipeHandlersEvent(typeMap));
		RecipeEventJS.instance = new RecipeEventJS(typeMap);
	}
	
	public PreparableReloadListener createReloadListener() {
		return (stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> {
			if (!(resourceManager instanceof SimpleReloadableResourceManager)) {
				throw new RuntimeException("Resource manager is not ReloadableResourceManagerImpl, KubeJS will not work! Unsupported resource manager class: " + resourceManager.getClass());
			}
			
			reloadScripts((SimpleReloadableResourceManager) resourceManager);
			return CompletableFuture.supplyAsync(Object::new, backgroundExecutor).thenCompose(stage::wait).thenAcceptAsync(o -> {}, gameExecutor);
		};
	}
}
