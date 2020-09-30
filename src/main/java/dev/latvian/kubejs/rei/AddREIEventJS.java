package dev.latvian.kubejs.rei;

import com.google.common.collect.Lists;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.ListJS;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.EntryStack;

import java.util.List;
import java.util.function.Function;

public class AddREIEventJS extends EventJS {
	private final EntryRegistry registry;
	private final Function<Object, EntryStack> function;
	private final List<EntryStack> added = Lists.newArrayList();
	
	public AddREIEventJS(EntryRegistry registry, Function<Object, EntryStack> function) {
		this.registry = registry;
		this.function = function;
	}
	
	public void add(Object o) {
		for (Object o1 : ListJS.orSelf(o)) {
			EntryStack stack = function.apply(o1);
			
			if (stack != null && !stack.isEmpty()) {
				added.add(stack);
			}
		}
	}
	
	@Override
	protected void afterPosted(boolean result) {
		if (!added.isEmpty()) {
			registry.registerEntriesAfter(null, added);
		}
	}
}
