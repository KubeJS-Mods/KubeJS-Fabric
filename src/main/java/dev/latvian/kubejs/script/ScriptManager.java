package dev.latvian.kubejs.script;

import com.google.common.base.Stopwatch;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.bindings.DefaultBindings;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.event.EventsJS;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.util.*;

/**
 * @author LatvianModder
 */
public class ScriptManager {
	private static final String[] BLOCKED_FUNCTIONS = {
			"print",
			"load",
			"loadWithNewGlobal",
			"exit",
			"quit"
	};
	
	public final ScriptType type;
	public final EventsJS events;
	public final Map<String, ScriptPack> packs;
	public final List<String> errors;
	
	public ScriptFile currentFile;
	public Map<String, Object> bindings, constants;
	
	public ScriptManager(ScriptType t) {
		type = t;
		events = new EventsJS(this);
		packs = new LinkedHashMap<>();
		errors = new ArrayList<>();
	}
	
	public void unload() {
		events.clear();
		packs.clear();
	}
	
	public void load() {
		errors.clear();
		bindings = new HashMap<>();
		constants = new HashMap<>();
		BindingsEvent event = new BindingsEvent(type, bindings, constants);
		BindingsEvent.EVENT.invoker().accept(event);
		DefaultBindings.init(this, event);
		Bindings b = new SimpleBindings();
		b.putAll(constants);
		b.putAll(bindings);
		
		int i = 0;
		int t = 0;
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		packs.values().stream()
				.flatMap(pack -> pack.scripts.stream())
				.parallel()
				.forEach(file -> file.babel.get());
		
		System.out.println("Babel Transformer done in " + stopwatch.stop().toString());
		stopwatch.reset().start();
		
		for (ScriptPack pack : packs.values()) {
			pack.engine = new NashornScriptEngineFactory().getScriptEngine(s -> false);
			
			ScriptContext context = pack.engine.getContext();
			
			for (String s : BLOCKED_FUNCTIONS) {
				context.removeAttribute(s, context.getAttributesScope(s));
			}
			
			for (ScriptFile file : pack.scripts) {
				t++;
				currentFile = file;
				
				if (file.getError() == null && file.load(b)) {
					i++;
					type.console.info("Loaded script " + file.info.location);
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
		
		events.postToHandlers(KubeJSEvents.LOADED, events.handlers(KubeJSEvents.LOADED), new EventJS());
		ScriptsLoadedEvent.EVENT.invoker().accept(new ScriptsLoadedEvent());
	}
}