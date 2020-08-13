package dev.latvian.kubejs.world;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

/**
 * @author LatvianModder
 */
public class WorldCommandSender extends ServerCommandSource {
	public WorldCommandSender(ServerWorldJS w) {
		super(CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, (ServerWorld) w.minecraftWorld, 4, "World", new LiteralText("World"), w.getServer().minecraftServer, null, true, (context, success, result) -> {}, EntityAnchorArgumentType.EntityAnchor.FEET);
	}
}