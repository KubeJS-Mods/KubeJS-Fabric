package dev.latvian.kubejs.player;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.stat.StatHandler;

/**
 * @author LatvianModder
 */
public class PlayerStatsJS {
	private final PlayerJS<?> player;
	private final StatHandler statFile;
	
	public PlayerStatsJS(PlayerJS<?> p, StatHandler s) {
		player = p;
		statFile = s;
	}
	
	public PlayerJS<?> getPlayer() {
		return player;
	}
	
	public int get(@ID String id) {
		return statFile.getStat(UtilsJS.getStat(id));
	}
	
	public void set(@ID String id, int value) {
		statFile.setStat(player.minecraftPlayer, UtilsJS.getStat(id), value);
	}
	
	public void add(@ID String id, int value) {
		statFile.increaseStat(player.minecraftPlayer, UtilsJS.getStat(id), value);
	}
}