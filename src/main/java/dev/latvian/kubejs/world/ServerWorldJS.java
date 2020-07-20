package dev.latvian.kubejs.world;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.FakeServerPlayerDataJS;
import dev.latvian.kubejs.player.ServerPlayerDataJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.LevelProperties;

/**
 * @author LatvianModder
 */
public class ServerWorldJS extends WorldJS {
	private final ServerJS server;
	
	public ServerWorldJS(ServerJS s, ServerWorld w) {
		super(w);
		server = s;
	}
	
	@Override
	public ScriptType getSide() {
		return ScriptType.SERVER;
	}
	
	@Override
	public ServerJS getServer() {
		return server;
	}
	
	public long getSeed() {
		return ((ServerWorld) minecraftWorld).getSeed();
	}
	
	public void setTime(long time) {
		((LevelProperties) minecraftWorld.getLevelProperties()).method_29034(time);
	}
	
	public void setLocalTime(long time) {
		((LevelProperties) minecraftWorld.getLevelProperties()).method_29035(time);
	}
	
	@Override
	public ServerPlayerDataJS getPlayerData(PlayerEntity player) {
		ServerPlayerDataJS data = server.playerMap.get(player.getUuid());
		
		if (data != null) {
			return data;
		}
		
		FakeServerPlayerDataJS fakeData = server.fakePlayerMap.get(player.getUuid());
		
		if (fakeData == null) {
			fakeData = new FakeServerPlayerDataJS(server, (ServerPlayerEntity) player);
			AttachPlayerDataEvent.EVENT.invoker().accept(new AttachPlayerDataEvent(fakeData));
		}
		
		fakeData.player = (ServerPlayerEntity) player;
		return fakeData;
	}
	
	@Override
	public String toString() {
		return "ServerWorld:" + getDimension();
	}
	
	@Override
	public EntityArrayList getEntities() {
		return new EntityArrayList(this, Lists.newArrayList(((ServerWorld) minecraftWorld).iterateEntities()));
	}
	
	public EntityArrayList getEntities(String filter) {
		try {
			return createEntityList(new EntitySelectorReader(new StringReader(filter), true).build().getEntities(new WorldCommandSender(this)));
		} catch (CommandSyntaxException e) {
			return new EntityArrayList(this, 0);
		}
	}
}