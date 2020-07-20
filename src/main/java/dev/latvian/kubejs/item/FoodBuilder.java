package dev.latvian.kubejs.item;

import com.google.common.collect.Lists;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class FoodBuilder {
	private int hunger;
	private float saturation;
	private boolean meat;
	private boolean alwaysEdible;
	private boolean fastToEat;
	private final List<Pair<Supplier<StatusEffectInstance>, Float>> effects = Lists.newArrayList();
	public Consumer<ItemFoodEatenEventJS> eaten;
	
	public FoodBuilder hunger(int h) {
		hunger = h;
		return this;
	}
	
	public FoodBuilder saturation(float s) {
		saturation = s;
		return this;
	}
	
	public FoodBuilder meat() {
		meat = true;
		return this;
	}
	
	public FoodBuilder alwaysEdible() {
		alwaysEdible = true;
		return this;
	}
	
	public FoodBuilder fastToEat() {
		fastToEat = true;
		return this;
	}
	
	public FoodBuilder effect(@ID String potion, int duration, int amplifier, float probability) {
		Identifier id = UtilsJS.getMCID(potion);
		effects.add(Pair.of(() -> new StatusEffectInstance(Registry.STATUS_EFFECT.get(id), duration, amplifier), probability));
		return this;
	}
	
	public FoodBuilder eaten(Consumer<ItemFoodEatenEventJS> e) {
		eaten = e;
		return this;
	}
	
	public FoodComponent build() {
		FoodComponent.Builder b = new FoodComponent.Builder();
		b.hunger(hunger);
		b.saturationModifier(saturation);
		
		if (meat) {
			b.meat();
		}
		
		if (alwaysEdible) {
			b.alwaysEdible();
		}
		
		if (fastToEat) {
			b.snack();
		}
		
		for (Pair<Supplier<StatusEffectInstance>, Float> effect : effects) {
			b.statusEffect(effect.getKey().get(), effect.getRight());
		}
		
		return b.build();
	}
}