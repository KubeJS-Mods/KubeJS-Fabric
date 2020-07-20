package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class EntityPotionEffectsJS {
	private final LivingEntity entity;
	
	public EntityPotionEffectsJS(LivingEntity e) {
		entity = e;
	}
	
	public void clear() {
		entity.clearStatusEffects();
	}
	
	public Collection<StatusEffectInstance> getActive() {
		return entity.getStatusEffects();
	}
	
	public Map<StatusEffect, StatusEffectInstance> getMap() {
		return entity.getActiveStatusEffects();
	}
	
	public boolean isActive(@ID String potion) {
		StatusEffect p = UtilsJS.getPotion(potion);
		return p != null && entity.hasStatusEffect(p);
	}
	
	@Nullable
	public StatusEffectInstance getActive(@ID String potion) {
		StatusEffect p = UtilsJS.getPotion(potion);
		return p == null ? null : entity.getStatusEffect(p);
	}
	
	public void add(@ID String potion) {
		add(potion, 0, 0);
	}
	
	public void add(@ID String potion, int duration) {
		add(potion, duration, 0);
	}
	
	public void add(@ID String potion, int duration, int amplifier) {
		add(potion, duration, amplifier, false, true);
	}
	
	public void add(@ID String potion, int duration, int amplifier, boolean ambient, boolean showParticles) {
		StatusEffect p = UtilsJS.getPotion(potion);
		
		if (p != null) {
			entity.addStatusEffect(new StatusEffectInstance(p, duration, amplifier, ambient, showParticles));
		}
	}
	
	public boolean isApplicable(StatusEffectInstance effect) {
		return entity.canHaveStatusEffect(effect);
	}
}