package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.*;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stat;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class UtilsWrapper {
	public ServerJS getServer() {
		return ServerJS.instance;
	}
	
	public void queueIO(Runnable runnable) {
		UtilsJS.queueIO(runnable);
	}
	
	public Random getRandom() {
		return UtilsJS.RANDOM;
	}
	
	public Random newRandom(long seed) {
		return new Random(seed);
	}
	
	public <T> List<T> emptyList() {
		return Collections.emptyList();
	}
	
	public <K, V> Map<K, V> emptyMap() {
		return Collections.emptyMap();
	}
	
	public ListJS newList() {
		return new ListJS();
	}
	
	public MapJS newMap() {
		return new MapJS();
	}
	
	public CountingMap newCountingMap() {
		return new CountingMap();
	}
	
	public Identifier id(String namespace, String path) {
		return new Identifier(namespace, path);
	}
	
	public Identifier id(@ID String id) {
		return UtilsJS.getMCID(id);
	}
	
	public ConsoleJS createConsole(String name) {
		return new ConsoleJS(ScriptType.STARTUP, LogManager.getLogger(name));
	}
	
	public Pattern regex(String pattern) {
		return Pattern.compile(pattern);
	}
	
	public Pattern regex(String pattern, int flags) {
		return Pattern.compile(pattern, flags);
	}

//	public <T> FieldJS<T> getField(String className, String fieldName)
//	{
//		return UtilsJS.getField(className, fieldName);
//	}
//
//	public <T> FieldJS<T> getField(Class className, String fieldName)
//	{
//		return UtilsJS.getField(className, fieldName);
//	}
	
	public int parseInt(@Nullable Object object, int def) {
		return UtilsJS.parseInt(object, def);
	}
	
	public double parseDouble(@Nullable Object object, double def) {
		return UtilsJS.parseDouble(object, def);
	}
	
	public Stat<Identifier> getStat(@ID String id) {
		return UtilsJS.getStat(id);
	}
	
	public Identifier getToolType(String id) {
		return UtilsJS.getToolType(id);
	}
	
	public WorldJS getWorld(World world) {
		if (world.isClient()) {
			return getClientWorld();
		} else {
			return ServerJS.instance.getWorld(world);
		}
	}
	
	public WorldJS getClientWorld() {
		return ClientWorldJS.instance;
	}
	
	@Nullable
	public SoundEvent getSound(@ID String id) {
		return Registry.SOUND_EVENT.get(UtilsJS.getMCID(id));
	}
	
	public Object randomOf(Random random, Collection<Object> objects) {
		if (objects.isEmpty()) {
			return null;
		}
		
		if (objects instanceof List) {
			return ((List) objects).get(random.nextInt(objects.size()));
		} else {
			return new ArrayList<>(objects).get(random.nextInt(objects.size()));
		}
	}
	
	public long getSystemTime() {
		return System.currentTimeMillis();
	}
	
	public Overlay overlay(String id, Object[] text) {
		Overlay o = new Overlay(id);
		
		for (Object o1 : text) {
			o.add(o1);
		}
		
		return o;
	}
	
	@Nullable
	public StatusEffect getPotion(@ID String id) {
		return UtilsJS.getPotion(id);
	}
	
	@Nullable
	public ListJS listOf(@Nullable Object o) {
		return ListJS.of(o);
	}
	
	public ListJS listOrSelf(@Nullable Object o) {
		return ListJS.orSelf(o);
	}
	
	@Nullable
	public MapJS mapOf(@Nullable Object o) {
		return MapJS.of(o);
	}
	
	@Nullable
	public Object copy(@Nullable Object o) {
		return UtilsJS.copy(o);
	}
}