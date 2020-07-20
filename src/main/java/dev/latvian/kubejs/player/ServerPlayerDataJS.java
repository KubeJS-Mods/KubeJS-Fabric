package dev.latvian.kubejs.player;

import com.mojang.authlib.GameProfile;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.world.ServerWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.server.network.ServerPlayerEntity;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ServerPlayerDataJS extends PlayerDataJS<ServerPlayerEntity, ServerPlayerJS> {
	private final ServerJS server;
	private final UUID id;
	private final String name;
	private final GameProfile profile;
	private final boolean hasClientMod;
	
	public ServerPlayerDataJS(ServerJS s, UUID i, String n, boolean h) {
		server = s;
		id = i;
		name = n;
		profile = new GameProfile(id, name);
		hasClientMod = h;
	}
	
	public ServerJS getServer() {
		return server;
	}
	
	@Override
	public UUID getId() {
		return id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public GameProfile getProfile() {
		return profile;
	}
	
	@Override
	public WorldJS getOverworld() {
		return server.getOverworld();
	}
	
	@Override
	@Nullable
	public ServerPlayerEntity getMinecraftPlayer() {
		return server.minecraftServer.getPlayerManager().getPlayer(getId());
	}
	
	@Override
	public ServerPlayerJS getPlayer() {
		ServerPlayerEntity p = getMinecraftPlayer();
		
		if (p == null) {
			throw new NullPointerException("Player entity for " + getName() + " not found!");
		}
		
		return new ServerPlayerJS(this, (ServerWorldJS) server.getWorld(p.world), p);
	}
	
	@Override
	public boolean hasClientMod() {
		return hasClientMod;
	}
}