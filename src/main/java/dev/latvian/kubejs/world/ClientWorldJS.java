package dev.latvian.kubejs.world;

import com.google.common.collect.Lists;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.player.ClientPlayerDataJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.script.ScriptType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @author LatvianModder
 */
@Environment(EnvType.CLIENT)
public class ClientWorldJS extends WorldJS {
	public static ClientWorldJS instance;
	
	private final MinecraftClient minecraft;
	@MinecraftClass
	public final ClientPlayerEntity minecraftPlayer;
	public final ClientPlayerDataJS clientPlayerData;
	
	public ClientWorldJS(MinecraftClient mc, ClientPlayerEntity e) {
		super(e.world);
		minecraft = mc;
		minecraftPlayer = e;
		clientPlayerData = new ClientPlayerDataJS(this, minecraftPlayer, true);
	}
	
	@MinecraftClass
	public MinecraftClient getMinecraft() {
		return minecraft;
	}
	
	@Override
	public ScriptType getSide() {
		return ScriptType.CLIENT;
	}
	
	@Override
	public ClientPlayerDataJS getPlayerData(PlayerEntity player) {
		if (player == minecraftPlayer || player.getUuid().equals(clientPlayerData.getId())) {
			return clientPlayerData;
		} else {
			return new ClientPlayerDataJS(this, player, false);
		}
	}
	
	@Override
	public String toString() {
		return "ClientWorld:" + getDimension();
	}
	
	@Override
	public EntityArrayList getEntities() {
		return new EntityArrayList(this, Lists.newArrayList(((ClientWorld) minecraftWorld).getEntities()));
	}
}