package dev.latvian.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.latvian.kubejs.server.ServerEventJS;
import net.minecraft.commands.CommandSourceStack;

/**
 * @author LatvianModder
 */
public class CommandRegistryEventJS extends ServerEventJS {
	private final boolean singlePlayer;
	private final CommandDispatcher<CommandSourceStack> dispatcher;
	
	public CommandRegistryEventJS(boolean s, CommandDispatcher<CommandSourceStack> c) {
		singlePlayer = s;
		dispatcher = c;
	}
	
	public boolean isSinglePlayer() {
		return singlePlayer;
	}
	
	public CommandDispatcher<CommandSourceStack> getDispatcher() {
		return dispatcher;
	}
}