package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * @author LatvianModder
 */
public class KubeJSNet
{
	public static final Identifier PACKET_ID_S2C = new Identifier(KubeJS.MOD_ID, "s2c");
	public static final Identifier PACKET_ID_C2S = new Identifier(KubeJS.MOD_ID, "c2s");

	public static void init()
	{
		ServerSidePacketRegistry.INSTANCE.register(PACKET_ID_C2S, (packetContext, packetByteBuf) -> {
			int id = packetByteBuf.readInt();
			switch (id)
			{
				case 0:
				{
					new MessageSendDataFromClient(packetByteBuf).handle(() -> packetContext);
					break;
				}
			}
		});
	}

	@Environment(EnvType.CLIENT)
	public static void sendToServer(MessageSendDataFromClient message)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(0);
		message.write(buf);
		ClientSidePacketRegistry.INSTANCE.sendToServer(PACKET_ID_C2S, buf);
	}

	public static void sendToPlayers(List<ServerPlayerEntity> playerList, MessageSendDataFromServer message)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(1);
		message.write(buf);
		for (ServerPlayerEntity entity : playerList)
		{
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(entity, PACKET_ID_S2C, buf);
		}
	}

	public static void sendToPlayers(List<ServerPlayerEntity> playerList, MessageOpenOverlay message)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(2);
		message.write(buf);
		for (ServerPlayerEntity entity : playerList)
		{
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(entity, PACKET_ID_S2C, buf);
		}
	}

	public static void sendToPlayers(List<ServerPlayerEntity> playerList, MessageCloseOverlay message)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(3);
		message.write(buf);
		for (ServerPlayerEntity entity : playerList)
		{
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(entity, PACKET_ID_S2C, buf);
		}
	}
}