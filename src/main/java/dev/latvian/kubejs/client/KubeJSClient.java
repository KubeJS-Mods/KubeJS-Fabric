package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSCommon;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.core.ResourcePackManagerKJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.net.*;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
@Environment(EnvType.CLIENT)
public class KubeJSClient extends KubeJSCommon implements ClientModInitializer {
	public static final Map<String, Overlay> activeOverlays = new LinkedHashMap<>();
	
	@Override
	public void init() {
		KubeJS.clientScriptManager.unload();
		KubeJS.clientScriptManager.loadFromDirectory();
		KubeJS.clientScriptManager.load();
		
		new KubeJSClientEventHandler().init();
		PackRepository manager = Minecraft.getInstance().getResourcePackRepository();
		((ResourcePackManagerKJS) manager).addProviderKJS(new KubeJSResourcePackFinder());
		
		ClientSidePacketRegistry.INSTANCE.register(KubeJSNet.PACKET_ID_S2C, (packetContext, packetByteBuf) -> {
			int id = packetByteBuf.readInt();
			switch (id) {
				case 1: {
					new MessageSendDataFromServer(packetByteBuf).handle(() -> packetContext);
					break;
				}
				case 2: {
					new MessageOpenOverlay(packetByteBuf).handle(() -> packetContext);
					break;
				}
				case 3: {
					new MessageCloseOverlay(packetByteBuf).handle(() -> packetContext);
					break;
				}
			}
		});
	}
	
	@Override
	public void clientBindings(BindingsEvent event) {
		event.add("client", new ClientWrapper());
	}
	
	@Override
	public void onInitializeClient() {
		new EventJS().post(ScriptType.CLIENT, KubeJSEvents.CLIENT_INIT);
	}
	
	@Override
	public void handleDataToClientPacket(String channel, @Nullable CompoundTag data) {
		new NetworkEventJS(Minecraft.getInstance().player, channel, MapJS.of(data)).post(KubeJSEvents.PLAYER_DATA_FROM_SERVER, channel);
	}
	
	@Override
	@Nullable
	public Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}
	
	@Override
	public void openOverlay(Overlay o) {
		activeOverlays.put(o.id, o);
	}
	
	@Override
	public void closeOverlay(String id) {
		activeOverlays.remove(id);
	}
	
	@Override
	public WorldJS getClientWorld() {
		return ClientWorldJS.instance;
	}
}