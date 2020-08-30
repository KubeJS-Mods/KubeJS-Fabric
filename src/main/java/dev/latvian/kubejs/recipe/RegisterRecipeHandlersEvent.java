package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.util.UtilsJS;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class RegisterRecipeHandlersEvent {
	public static final Event<Consumer<RegisterRecipeHandlersEvent>> EVENT = EventFactory.createArrayBacked(Consumer.class, consumers -> event -> {
		for (Consumer<RegisterRecipeHandlersEvent> consumer : consumers) {
			consumer.accept(event);
		}
	});
	private final Map<ResourceLocation, RecipeTypeJS> map;
	
	public RegisterRecipeHandlersEvent(Map<ResourceLocation, RecipeTypeJS> m) {
		map = m;
	}
	
	public void register(RecipeTypeJS type) {
		map.put(UtilsJS.getMCID(type.toString()), type);
	}
}