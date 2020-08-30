package dev.latvian.kubejs.world;

import com.google.common.collect.Lists;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.player.ClientPlayerDataJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.script.ScriptType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
@Environment(EnvType.CLIENT)
public class ClientWorldJS extends WorldJS {
	public static ClientWorldJS instance;
	
	private final Minecraft minecraft;
	@MinecraftClass
	public final LocalPlayer minecraftPlayer;
	public final ClientPlayerDataJS clientPlayerData;
	
	public ClientWorldJS(Minecraft mc, LocalPlayer e) {
		super(e.level);
		minecraft = mc;
		minecraftPlayer = e;
		clientPlayerData = new ClientPlayerDataJS(this, minecraftPlayer, true);
	}
	
	@MinecraftClass
	public Minecraft getMinecraft() {
		return minecraft;
	}
	
	@Override
	public ScriptType getSide() {
		return ScriptType.CLIENT;
	}
	
	@Override
	public ClientPlayerDataJS getPlayerData(Player player) {
		if (player == minecraftPlayer || player.getUUID().equals(clientPlayerData.getId())) {
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
		return new EntityArrayList(this, Lists.newArrayList(((ClientLevel) minecraftWorld).entitiesForRendering()));
	}
}