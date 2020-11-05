package dev.latvian.kubejs.script;

import dev.latvian.mods.rhino.*;
import dev.latvian.mods.rhino.util.DynamicFunction;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BindingsEvent {
	public static final Event<Consumer<BindingsEvent>> EVENT = EventFactory.createArrayBacked(Consumer.class, consumers -> event -> {
		for (Consumer<BindingsEvent> consumer : consumers) {
			consumer.accept(event);
		}
	});
	
	public final ScriptType type;
	public Scriptable scope;
	
	public BindingsEvent(ScriptType t, Scriptable s) {
		type = t;
		scope = s;
	}
	
	public ScriptType getType() {
		return type;
	}
	
	public void add(String name, Object value) {
		ScriptableObject.putProperty(scope, name, Context.javaToJS(value, scope));
	}
	
	public void addClass(String name, Class<?> clazz) {
		add(name, new NativeJavaClass(scope, clazz));
	}
	
	public void addFunction(String name, DynamicFunction.Callback function) {
		add(name, new DynamicFunction(function));
	}
	
	public void addConstant(String name, Object value) {
		add(name, value);
	}
	
	public void addFunction(String name, BaseFunction function) {
		add(name, function);
	}
}