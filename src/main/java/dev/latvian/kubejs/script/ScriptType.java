package dev.latvian.kubejs.script;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.server.ServerScriptManager;
import dev.latvian.kubejs.util.ConsoleJS;
import net.minecraft.world.WorldView;
import org.apache.logging.log4j.LogManager;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public enum ScriptType {
	STARTUP("startup", "KubeJS Startup", () -> KubeJS.startupScriptManager),
	SERVER("server", "KubeJS Server", () -> ServerScriptManager.instance.scriptManager),
	CLIENT("client", "KubeJS Client", () -> KubeJS.clientScriptManager);
	
	public static ScriptType of(WorldView world) {
		return world.isClient() ? CLIENT : SERVER;
	}
	
	public final String name;
	public final ConsoleJS console;
	public final Supplier<ScriptManager> manager;
	
	ScriptType(String n, String cname, Supplier<ScriptManager> m) {
		name = n;
		console = new ConsoleJS(this, LogManager.getLogger(cname));
		manager = m;
	}
}