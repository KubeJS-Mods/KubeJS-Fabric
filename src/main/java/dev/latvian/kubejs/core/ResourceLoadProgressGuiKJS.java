package dev.latvian.kubejs.core;

import dev.latvian.kubejs.client.ClientProperties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * @author LatvianModder
 */
@Environment(EnvType.CLIENT)
public interface ResourceLoadProgressGuiKJS {
	default int getBackgroundColorKJS(int color) {
		return ClientProperties.get().overrideColors ? ((color & 0xFF000000) | ClientProperties.get().backgroundColor) : color;
	}
	
	default int getBarColorKJS(int color) {
		return ClientProperties.get().overrideColors ? ((color & 0xFF000000) | ClientProperties.get().barColor) : color;
	}
	
	default int getBarBorderColorKJS(int color) {
		return ClientProperties.get().overrideColors ? ((color & 0xFF000000) | ClientProperties.get().barBorderColor) : color;
	}
}
