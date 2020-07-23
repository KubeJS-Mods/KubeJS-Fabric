package dev.latvian.kubejs;

import dev.latvian.kubejs.block.BlockRegistryEventJS;
import dev.latvian.kubejs.block.KubeJSBlockEventHandler;
import dev.latvian.kubejs.core.AfterScriptLoadCallback;
import dev.latvian.kubejs.entity.KubeJSEntityEventHandler;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.fluid.FluidRegistryEventJS;
import dev.latvian.kubejs.fluid.KubeJSFluidEventHandler;
import dev.latvian.kubejs.item.ItemRegistryEventJS;
import dev.latvian.kubejs.item.KubeJSItemEventHandler;
import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.player.KubeJSPlayerEventHandler;
import dev.latvian.kubejs.recipe.KubeJSRecipeEventHandler;
import dev.latvian.kubejs.script.*;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.KubeJSWorldEventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Locale;

/**
 * @author LatvianModder
 */
public class KubeJS implements ModInitializer {
	public static KubeJS instance;
	public static final String MOD_ID = "kubejs";
	public static final String MOD_NAME = "KubeJS";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	
	public final KubeJSCommon proxy;
	public static boolean nextClientHasClientMod = false;
	
	public static ScriptManager startupScriptManager, clientScriptManager;
	
	public KubeJS() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Locale.setDefault(Locale.US);
		
		instance = this;
		startupScriptManager = new ScriptManager(ScriptType.STARTUP);
		clientScriptManager = new ScriptManager(ScriptType.CLIENT);
		// TODO Completely remove proxy, this is a bad hack in fabric but we are doing this to make porting easier.
		String proxyClass = "dev.latvian.kubejs." + (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? "client.KubeJSClient" : "KubeJSCommon");
		proxy = (KubeJSCommon) Class.forName(proxyClass).getDeclaredConstructor().newInstance();
		
		// TODO add back
//		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		
		new KubeJSOtherEventHandler().init();
		new KubeJSWorldEventHandler().init();
		new KubeJSPlayerEventHandler().init();
		new KubeJSEntityEventHandler().init();
		new KubeJSBlockEventHandler().init();
		new KubeJSItemEventHandler().init();
		new KubeJSRecipeEventHandler().init();
		new KubeJSFluidEventHandler().init();
		
		File folder = getGameDirectory().resolve("kubejs").toFile();
		
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		proxy.init(folder);
		
		File startupFolder = new File(folder, "startup");
		
		if (!startupFolder.exists()) {
			startupFolder.mkdirs();
			
			try {
				try (PrintWriter exampleJsWriter = new PrintWriter(new FileWriter(new File(startupFolder, "example.js")))) {
					exampleJsWriter.println("console.info('Hello, World! (You will only see this line once in console, during startup)')");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		startupScriptManager.unload();
		
		if (new File(startupFolder, "scripts.json").exists()) {
			LOGGER.warn("KubeJS no longer uses scripts.json file, please delete it! To reorder scripts, add '// priority: 10' on top of them. Default priority is 0.");
		}
		
		ScriptPack pack = new ScriptPack(startupScriptManager, new ScriptPackInfo("startup", ""));
		loadScripts(pack, startupFolder, "");
		
		for (ScriptFileInfo fileInfo : pack.info.scripts) {
			ScriptSource scriptSource = info -> new FileReader(new File(startupFolder, info.file));
			
			Throwable error = fileInfo.preload(scriptSource);
			
			if (error == null) {
				pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
			} else {
				LOGGER.error("Failed to pre-load script file " + fileInfo.location + ": " + error);
			}
		}
		
		pack.scripts.sort(null);
		startupScriptManager.packs.put(pack.info.namespace, pack);
		
		startupScriptManager.load();
		
		new BlockRegistryEventJS().post(ScriptType.STARTUP, KubeJSEvents.BLOCK_REGISTRY);
		new ItemRegistryEventJS().post(ScriptType.STARTUP, KubeJSEvents.ITEM_REGISTRY);
		new FluidRegistryEventJS().post(ScriptType.STARTUP, KubeJSEvents.FLUID_REGISTRY);
		
		AfterScriptLoadCallback.EVENT.invoker().afterLoad();
	}
	
	private void loadScripts(ScriptPack pack, File dir, String path) {
		File[] files = dir.listFiles();
		
		if (files != null && files.length > 0) {
			for (File file : files) {
				if (file.isDirectory()) {
					loadScripts(pack, file, path.isEmpty() ? file.getName() : (path + "/" + file.getName()));
				} else if (file.getName().endsWith(".js")) {
					pack.info.scripts.add(new ScriptFileInfo(pack.info, path.isEmpty() ? file.getName() : (path + "/" + file.getName())));
				}
			}
		}
	}
	
	public static String appendModId(String id) {
		return id.indexOf(':') == -1 ? (MOD_ID + ":" + id) : id;
	}
	
	public static Path getGameDirectory() {
		return FabricLoader.getInstance().getGameDirectory().toPath();
	}
	
	public static void verifyFilePath(Path path) throws IOException {
		if (!path.normalize().toAbsolutePath().startsWith(getGameDirectory())) {
			throw new IOException("You can't access files outside Minecraft directory!");
		}
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

//	private void loadComplete(FMLLoadCompleteEvent event)
//	{
//		new EventJS().post(ScriptType.STARTUP, KubeJSEvents.POSTINIT);
//	}
}