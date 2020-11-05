package dev.latvian.kubejs;

import dev.latvian.kubejs.block.BlockRegistryEventJS;
import dev.latvian.kubejs.core.AfterScriptLoadCallback;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.fluid.FluidRegistryEventJS;
import dev.latvian.kubejs.item.ItemRegistryEventJS;
import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.script.ScriptFileInfo;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptPack;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.trade.TradeRegistryEventJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author LatvianModder
 */
public class KubeJS implements ModInitializer {
	public static final String MOD_ID = "kubejs";
	public static final String MOD_NAME = "KubeJS";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	
	public static KubeJS instance;
	
	public final KubeJSCommon proxy;
	public static boolean nextClientHasClientMod = false;
	
	public static ScriptManager startupScriptManager, clientScriptManager;
	
	public KubeJS() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Locale.setDefault(Locale.US);
		
		instance = this;
		
		if (Files.notExists(KubeJSPaths.README)) {
			UtilsJS.tryIO(() -> {
				List<String> list = new ArrayList<>();
				list.add("Find more info on the website: https://kubejs.latvian.dev/");
				list.add("");
				list.add("Directory information:");
				list.add("");
				list.add("assets - Acts as a resource pack, you can put any client resources in here, like textures, models, etc. Example: assets/kubejs/textures/item/test_item.png");
				list.add("data - Acts as a datapack, you can put any server resources in here, like loot tables, functions, etc. Example: data/kubejs/loot_tables/blocks/test_block.json");
				list.add("");
				list.add("startup_scripts - Scripts that get loaded once during game startup - Used for adding items and other things");
				list.add("server_scripts - Scripts that get loaded every time server resources reload - Used for modifying recipes, tags, and handling server events");
				list.add("client_scripts - Scripts that get loaded every time client resources reload - Used for JEI events, tooltips and other client side things");
				list.add("");
				list.add("config - KubeJS config storage. This is also the only directory that scripts can access other than world directory");
				list.add("exported - Data dumps like texture atlases end up here");
				Files.write(KubeJSPaths.README, list);
			});
		}
		
		startupScriptManager = new ScriptManager(ScriptType.STARTUP, KubeJSPaths.STARTUP_SCRIPTS, "/data/kubejs/example_startup_script.js");
		clientScriptManager = new ScriptManager(ScriptType.CLIENT, KubeJSPaths.CLIENT_SCRIPTS, "/data/kubejs/example_client_script.js");
		// TODO Completely remove proxy, this is a bad hack in fabric but we are doing this to make porting easier.
		String proxyClass = "dev.latvian.kubejs." + (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? "client.KubeJSClient" : "KubeJSCommon");
		proxy = (KubeJSCommon) Class.forName(proxyClass).getDeclaredConstructor().newInstance();
		
		AfterScriptLoadCallback.EVENT.register(this::loadComplete);
		
		FabricLoader.getInstance().getEntrypoints("kubejs-init", KubeJSInitializer.class).forEach((it) -> {
			it.onKubeJSInitialization();
			LOGGER.log(Level.INFO, "Initialized entrypoint " + it.getClass().getSimpleName() + ".");
		});
		
		Path oldStartupFolder = KubeJSPaths.DIRECTORY.resolve("startup");
		
		if (Files.exists(oldStartupFolder)) {
			UtilsJS.tryIO(() -> Files.move(oldStartupFolder, KubeJSPaths.STARTUP_SCRIPTS));
		}
		
		startupScriptManager.unload();
		
		startupScriptManager.loadFromDirectory();
		startupScriptManager.load();
		
		proxy.init();
		
		new BlockRegistryEventJS().post(ScriptType.STARTUP, KubeJSEvents.BLOCK_REGISTRY);
		new ItemRegistryEventJS().post(ScriptType.STARTUP, KubeJSEvents.ITEM_REGISTRY);
		new FluidRegistryEventJS().post(ScriptType.STARTUP, KubeJSEvents.FLUID_REGISTRY);
		new TradeRegistryEventJS().post(ScriptType.STARTUP, KubeJSEvents.TRADE_REGISTRY);
		
		AfterScriptLoadCallback.EVENT.invoker().afterLoad();
	}
	
	public static void loadScripts(ScriptPack pack, Path dir, String path) {
		if (!path.isEmpty() && !path.endsWith("/")) {
			path += "/";
		}
		
		final String pathPrefix = path;
		
		UtilsJS.tryIO(() -> Files.walk(dir, 10).filter(Files::isRegularFile).forEach(file -> {
			String fileName = dir.relativize(file).toString().replace(File.separatorChar, '/');
			
			if (fileName.endsWith(".js")) {
				pack.info.scripts.add(new ScriptFileInfo(pack.info, pathPrefix + fileName));
			}
		}));
	}
	
	public static String appendModId(String id) {
		return id.indexOf(':') == -1 ? (MOD_ID + ":" + id) : id;
	}
	
	public static Path getGameDirectory() {
		return FabricLoader.getInstance().getGameDirectory().toPath();
	}
	
	public static Path verifyFilePath(Path path) throws IOException {
		if (!path.normalize().toAbsolutePath().startsWith(getGameDirectory())) {
			throw new IOException("You can't access files outside Minecraft directory!");
		}
		
		return path;
	}
	
	public static void verifyFilePath(File file) throws IOException {
		verifyFilePath(file.toPath());
	}
	
	@Override
	public void onInitialize() {
		UtilsJS.init();
		KubeJSNet.init();
		new EventJS().post(ScriptType.STARTUP, KubeJSEvents.INIT);
	}
	
	private void loadComplete() {
		new EventJS().post(ScriptType.STARTUP, KubeJSEvents.POSTINIT);
	}
}