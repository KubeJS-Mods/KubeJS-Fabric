package dev.latvian.kubejs.core;

import dev.latvian.kubejs.client.ClientProperties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * @author LatvianModder
 */
@Environment(EnvType.CLIENT)
public interface EarlyLoaderGUIKJS {
	default float[] getMemoryColorKJS(float[] color) {
		return ClientProperties.get().overrideColors ? ClientProperties.get().fmlMemoryColor3f : color;
	}
	
	default float[] getLogColorKJS(float[] color) {
		return ClientProperties.get().overrideColors ? ClientProperties.get().fmlLogColor3f : color;
	}
	
	default float getBackgroundColorKJS(float c, int index) {
		return ClientProperties.get().overrideColors ? ClientProperties.get().backgroundColor3f[index] : c;
	}
}
