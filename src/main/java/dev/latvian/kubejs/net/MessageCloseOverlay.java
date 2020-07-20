package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class MessageCloseOverlay {
	private final String overlay;
	
	public MessageCloseOverlay(String o) {
		overlay = o;
	}
	
	public MessageCloseOverlay(PacketByteBuf buf) {
		overlay = buf.readString(5000);
	}
	
	public void write(PacketByteBuf buf) {
		buf.writeString(overlay, 5000);
	}
	
	public void handle(Supplier<PacketContext> context) {
		context.get().getTaskQueue().execute(() -> KubeJS.instance.proxy.closeOverlay(overlay));
	}
}