package dev.latvian.kubejs.client;

import dev.latvian.kubejs.event.EventJS;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.util.List;

/**
 * @author LatvianModder
 */
@Environment(EnvType.CLIENT)
public class DebugInfoEventJS extends EventJS {
	public final List<String> lines;
	
	public DebugInfoEventJS(List<String> lines) {
		this.lines = lines;
	}
	
	public boolean getShowDebug() {
		return MinecraftClient.getInstance().options.debugEnabled;
	}
	
	public List<String> getLines() {
		return lines;
	}
}