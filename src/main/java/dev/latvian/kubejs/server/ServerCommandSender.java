package dev.latvian.kubejs.server;

import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

/**
 * @author LatvianModder
 */
public class ServerCommandSender extends ServerCommandSource
{
	public ServerCommandSender(ServerJS w)
	{
		super(CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, (ServerWorld) w.getOverworld().minecraftWorld, 4, "Server", new LiteralText("Server"), w.minecraftServer, null, true, (context, success, result) -> {}, EntityAnchorArgumentType.EntityAnchor.FEET);
	}
}