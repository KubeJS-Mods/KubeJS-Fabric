package dev.latvian.kubejs.server;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * @author LatvianModder
 */
public class ServerCommandSender extends CommandSourceStack {
	public ServerCommandSender(ServerJS w) {
		super(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, (ServerLevel) w.getOverworld().minecraftWorld, 4, "Server", new TextComponent("Server"), w.minecraftServer, null, true, (context, success, result) -> {}, EntityAnchorArgument.Anchor.FEET);
	}
}