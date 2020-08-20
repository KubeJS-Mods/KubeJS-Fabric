package dev.latvian.kubejs.script;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.core.util.IOUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class BabelExecutor {
	private static boolean inited = false;
	private static ScriptEngine scriptEngine;
	private static SimpleBindings bindings;
	
	private static boolean enabled() {
		return !Files.exists(FabricLoader.getInstance().getGameDir().resolve("kubejs/.disablebabel"));
	}
	
	private static void init() {
		if (inited) {
			return;
		}
		
		inited = true;
		scriptEngine = new NashornScriptEngineFactory().getScriptEngine();
		bindings = new SimpleBindings();
		
		try (BufferedReader babelScript = Files.newBufferedReader(FabricLoader.getInstance().getModContainer("kubejs").get().getPath("babel.min.js"), StandardCharsets.UTF_8)) {
			try {
				scriptEngine.eval(babelScript, bindings);
			} catch (ScriptException e) {
				throw new RuntimeException(e);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String process(Reader reader) throws IOException, ScriptException {
		if (!enabled())
			return IOUtils.toString(reader);
		init();
		bindings.put("input", IOUtils.toString(reader));
		return scriptEngine.eval("Babel.transform(input, { presets: ['es2015'], sourceMaps: 'inline' }).code", bindings).toString();
	}
}