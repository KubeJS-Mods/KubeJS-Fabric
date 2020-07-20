package dev.latvian.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.latvian.kubejs.server.ServerEventJS;
import net.minecraft.server.command.ServerCommandSource;

/**
 * @author LatvianModder
 */
public class CommandRegistryEventJS extends ServerEventJS {
	private final boolean singlePlayer;
	private final CommandDispatcher<ServerCommandSource> dispatcher;
	
	public CommandRegistryEventJS(boolean s, CommandDispatcher<ServerCommandSource> c) {
		singlePlayer = s;
		dispatcher = c;
	}
	
	public boolean isSinglePlayer() {
		return singlePlayer;
	}
	
	public CommandDispatcher<ServerCommandSource> getDispatcher() {
		return dispatcher;
	}
}