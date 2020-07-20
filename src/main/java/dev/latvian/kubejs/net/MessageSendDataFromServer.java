package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class MessageSendDataFromServer {
	private final String channel;
	private final CompoundTag data;
	
	public MessageSendDataFromServer(String c, @Nullable CompoundTag d) {
		channel = c;
		data = d;
	}
	
	public MessageSendDataFromServer(PacketByteBuf buf) {
		channel = buf.readString(120);
		data = buf.readCompoundTag();
	}
	
	public void write(PacketByteBuf buf) {
		buf.writeString(channel, 120);
		buf.writeCompoundTag(data);
	}
	
	public void handle(Supplier<PacketContext> context) {
		if (!channel.isEmpty()) {
			context.get().getTaskQueue().execute(() -> KubeJS.instance.proxy.handleDataToClientPacket(channel, data));
		}
	}
}