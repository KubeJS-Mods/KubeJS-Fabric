package dev.latvian.kubejs.recipe;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.util.UtilsJS;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
	
	public void register(@ID String id, Supplier<RecipeJS> f)
	{
		register(new RecipeTypeJS(Objects.requireNonNull(Registry.RECIPE_SERIALIZER.get(UtilsJS.getMCID(id))), f));
	}
}