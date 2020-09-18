package dev.latvian.kubejs.script;

import com.google.common.base.Stopwatch;
import dev.latvian.kubejs.CommonProperties;
import dev.latvian.kubejs.KubeJS;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.IOUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class BabelExecutor {
	private static boolean inited = false;
	private static ScriptEngine scriptEngine;
	private static SimpleBindings bindings;
	
	public static void init() {
		if (inited || !CommonProperties.get().enableES6) {
			return;
		}
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		inited = true;
		scriptEngine = new NashornScriptEngineFactory().getScriptEngine();
		bindings = new SimpleBindings();
		
		try (InputStream stream = Files.newInputStream(FabricLoader.getInstance().getModContainer("kubejs").get().getPath("babel.min.js"))) {
			String script = new String(IOUtils.toByteArray(new BufferedInputStream(stream)), StandardCharsets.UTF_8);
			scriptEngine.eval(script, bindings);
		} catch (ScriptException | IOException e) {
			throw new RuntimeException(e);
		}
		
		stopwatch.stop();
		KubeJS.LOGGER.info("Loaded babel.min.js in " + stopwatch.toString());
	}
	
	public static String process(String string) throws ScriptException {
		if (!CommonProperties.get().enableES6)
			return string;
		init();
		bindings.put("input", string);
		return scriptEngine.eval("Babel.transform(input, { presets: ['es2015'], sourceMaps: true, retainLines: true, sourceType: 'script' }).code", bindings).toString();
	}
}