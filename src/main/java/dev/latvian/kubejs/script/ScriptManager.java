package dev.latvian.kubejs.script;

import com.google.common.base.Stopwatch;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.bindings.DefaultBindings;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.ClassShutter;
import dev.latvian.mods.rhino.Context;
import org.apache.commons.io.IOUtils;

import javax.script.ScriptException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ScriptManager {
	public final ScriptType type;
	public final Path directory;
	public final String exampleScript;
	public final EventsJS events;
	public final Map<String, ScriptPack> packs;
	public final List<String> errors;
	
	public ScriptFile currentFile;
	public Map<String, Object> bindings;
	
	public ScriptManager(ScriptType t, Path p, String e) {
		type = t;
		directory = p;
		exampleScript = e;
		events = new EventsJS(this);
		packs = new LinkedHashMap<>();
		errors = new ArrayList<>();
	}
	
	public void unload() {
		events.clear();
		packs.clear();
	}
	
	public void loadFromDirectory() {
		if (Files.notExists(directory)) {
			UtilsJS.tryIO(() -> Files.createDirectories(directory));
			
			try (InputStream in = KubeJS.class.getResourceAsStream(exampleScript);
			     OutputStream out = Files.newOutputStream(directory.resolve("script.js"))) {
				out.write(IOUtils.toByteArray(in));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		ScriptPack pack = new ScriptPack(this, new ScriptPackInfo(directory.getFileName().toString(), ""));
		KubeJS.loadScripts(pack, directory, "");
		
		for (ScriptFileInfo fileInfo : pack.info.scripts) {
			ScriptSource.FromPath scriptSource = info -> directory.resolve(info.file);
			
			Throwable error = fileInfo.preload(scriptSource);
			
			if (error == null) {
				pack.scripts.add(new ScriptFile(pack, fileInfo, scriptSource));
			} else {
				KubeJS.LOGGER.error("Failed to pre-load script file " + fileInfo.location + ": " + error);
			}
		}
		
		pack.scripts.sort(null);
		packs.put(pack.info.namespace, pack);
	}
	
	public void load() {
		Context context = Context.enter();
		context.setLanguageVersion(Context.VERSION_ES6);
		context.setClassShutter((fullClassName, type) -> type != ClassShutter.TYPE_CLASS_IN_PACKAGE);
		
		errors.clear();
		
		int i = 0;
		int t = 0;
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		packs.values().stream()
				.flatMap(pack -> pack.scripts.stream())
				.parallel()
				.forEach(file -> file.content.get());
		
		System.out.println("Babel Transformer done in " + stopwatch.stop().toString());
		stopwatch.reset().start();
		
		for (ScriptPack pack : packs.values()) {
			pack.context = context;
			pack.scope = context.initStandardObjects();
			
			BindingsEvent event = new BindingsEvent(type, pack.scope);
			BindingsEvent.EVENT.invoker().accept(event);
			DefaultBindings.init(this, event);
			
			for (ScriptFile file : pack.scripts) {
				t++;
				currentFile = file;
				Stopwatch fileStopwatch = Stopwatch.createStarted();
				
				if (file.getError() == null && file.load()) {
					i++;
					type.console.info("Loaded script " + file.info.location + " in " + fileStopwatch.stop().toString());
				} else if (file.getError() != null) {
					type.console.error("Error loading KubeJS script " + file.info.location + ": " + file.getError().toString().replace("javax.script.ScriptException: ", ""));
					
					if (!(file.getError() instanceof ScriptException)) {
						file.getError().printStackTrace();
					}
					
					errors.add(file.info.location + ": " + file.getError().toString().replace("javax.script.ScriptException: ", ""));
				}
			}
		}
		
		currentFile = null;
		
		if (i == t) {
			type.console.info("Loaded " + i + "/" + t + " KubeJS " + type.name + " scripts in " + stopwatch.stop().toString());
		} else {
			type.console.error("Loaded " + i + "/" + t + " KubeJS " + type.name + " scripts in " + stopwatch.stop().toString());
		}
		
		Context.exit();
		
		events.postToHandlers(KubeJSEvents.LOADED, events.handlers(KubeJSEvents.LOADED), new EventJS());
		ScriptsLoadedEvent.EVENT.invoker().accept(new ScriptsLoadedEvent());
	}
}