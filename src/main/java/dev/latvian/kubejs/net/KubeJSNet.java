package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * @author LatvianModder
 */
public class KubeJSNet {
	public static final ResourceLocation PACKET_ID_S2C = new ResourceLocation(KubeJS.MOD_ID, "s2c");
	public static final ResourceLocation PACKET_ID_C2S = new ResourceLocation(KubeJS.MOD_ID, "c2s");
	
	public static void init() {
		ServerSidePacketRegistry.INSTANCE.register(PACKET_ID_C2S, (packetContext, packetByteBuf) -> {
			int id = packetByteBuf.readInt();
			switch (id) {
				case 0: {
					new MessageSendDataFromClient(packetByteBuf).handle(() -> packetContext);
					break;
				}
			}
		});
	}
	
	@Environment(EnvType.CLIENT)
	public static void sendToServer(MessageSendDataFromClient message) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeInt(0);
		message.write(buf);
		ClientSidePacketRegistry.INSTANCE.sendToServer(PACKET_ID_C2S, buf);
	}
	
	public static void sendToPlayers(List<ServerPlayer> playerList, MessageSendDataFromServer message) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeInt(1);
		message.write(buf);
		for (ServerPlayer entity : playerList) {
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(entity, PACKET_ID_S2C, buf);
		}
	}
	
	public static void sendToPlayers(List<ServerPlayer> playerList, MessageOpenOverlay message) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeInt(2);
		message.write(buf);
		for (ServerPlayer entity : playerList) {
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(entity, PACKET_ID_S2C, buf);
		}
	}
	
	public static void sendToPlayers(List<ServerPlayer> playerList, MessageCloseOverlay message) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeInt(3);
		message.write(buf);
		for (ServerPlayer entity : playerList) {
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(entity, PACKET_ID_S2C, buf);
		}
	}
}