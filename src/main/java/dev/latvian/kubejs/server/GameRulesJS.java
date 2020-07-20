package dev.latvian.kubejs.server;

import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.GameRules;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class GameRulesJS
{
	private GameRules rules;
	private Map<String, GameRules.Key> cache;

	public GameRulesJS(GameRules r)
	{
		rules = r;
	}

	@Nullable
	private GameRules.Key getKey(String rule)
	{
		if (cache == null)
		{
			cache = new HashMap<>();

			GameRules.forEachType(new GameRules.TypeConsumer()
			{
				@Override
				public <T extends GameRules.Rule<T>> void accept(GameRules.Key<T> key, GameRules.Type<T> type)
				{
					cache.put(key.toString(), key);
				}
			});
		}

		return cache.get(rule);
	}

	@Nullable
	private Object get(String rule)
	{
		GameRules.Key key = getKey(rule);
		return key == null ? null : rules.get(key);
	}

	public String getString(String rule)
	{
		Object o = get(rule);
		return o == null ? "" : String.valueOf(o);
	}

	public boolean getBoolean(String rule)
	{
		Object o = get(rule);
		return o instanceof Boolean && (Boolean) o;
	}

	public int getInt(String rule)
	{
		Object o = get(rule);
		return o instanceof Number ? ((Number) o).intValue() : 0;
	}

	public void set(String rule, Object value)
	{
		CompoundTag nbt = rules.toNbt();
		nbt.putString(rule, String.valueOf(value));
		rules = new GameRules(new Dynamic<>(NbtOps.INSTANCE, nbt)); //TODO: Check if works
	}
}