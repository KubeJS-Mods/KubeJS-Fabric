package dev.latvian.kubejs.util;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class DynamicConcurrentMapJS<K, V> extends ConcurrentHashMap<K, V> implements WrappedJS {
	private final Function<K, ? extends V> objectProvider;
	
	public DynamicConcurrentMapJS(Function<K, ? extends V> o) {
		objectProvider = o;
	}
	
	@Override
	@Nonnull
	public V get(Object key) {
		return super.computeIfAbsent((K) key, objectProvider);
	}
	
	@Override
	public boolean containsKey(Object name) {
		return true;
	}
}