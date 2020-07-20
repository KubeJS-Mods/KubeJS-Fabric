package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class LivingEntityJS extends EntityJS {
	@MinecraftClass
	public final LivingEntity minecraftLivingEntity;
	
	public LivingEntityJS(WorldJS w, LivingEntity e) {
		super(w, e);
		minecraftLivingEntity = e;
	}
	
	@Override
	public boolean isLiving() {
		return true;
	}
	
	public boolean isChild() {
		return minecraftLivingEntity.isBaby();
	}
	
	public float getHealth() {
		return minecraftLivingEntity.getHealth();
	}
	
	public void setHealth(float hp) {
		minecraftLivingEntity.setHealth(hp);
	}
	
	public void heal(float hp) {
		minecraftLivingEntity.heal(hp);
	}
	
	public float getMaxHealth() {
		return minecraftLivingEntity.getMaxHealth();
	}
	
	public void setMaxHealth(float hp) {
		minecraftLivingEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(hp);
	}
	
	public boolean isUndead() {
		return minecraftLivingEntity.isUndead();
	}
	
	public boolean isOnLadder() {
		return minecraftLivingEntity.isClimbing();
	}
	
	public boolean isSleeping() {
		return minecraftLivingEntity.isSleeping();
	}
	
	public boolean isElytraFlying() {
		return minecraftLivingEntity.isFallFlying();
	}
	
	@Nullable
	public LivingEntityJS getRevengeTarget() {
		return getWorld().getLivingEntity(minecraftLivingEntity.getAttacker());
	}
	
	public int getRevengeTimer() {
		return minecraftLivingEntity.getLastAttackedTime();
	}
	
	public void setRevengeTarget(@Nullable LivingEntityJS target) {
		minecraftLivingEntity.setAttacker(target == null ? null : target.minecraftLivingEntity);
	}
	
	@Nullable
	public LivingEntityJS getLastAttackedEntity() {
		return getWorld().getLivingEntity(minecraftLivingEntity.getAttacking());
	}
	
	public int getLastAttackedEntityTime() {
		return minecraftLivingEntity.getLastAttackTime();
	}
	
	public int getIdleTime() {
		return minecraftLivingEntity.getDespawnCounter();
	}
	
	public EntityPotionEffectsJS getPotionEffects() {
		return new EntityPotionEffectsJS(minecraftLivingEntity);
	}
	
	@Nullable
	public DamageSourceJS getLastDamageSource() {
		return minecraftLivingEntity.getRecentDamageSource() == null ? null : new DamageSourceJS(getWorld(), minecraftLivingEntity.getRecentDamageSource());
	}
	
	@Nullable
	public LivingEntityJS getAttackingEntity() {
		return getWorld().getLivingEntity(minecraftLivingEntity.getPrimeAdversary());
	}
	
	public void swingArm(Hand hand) {
		minecraftLivingEntity.swingHand(hand);
	}
	
	public ItemStackJS getEquipment(EquipmentSlot slot) {
		return ItemStackJS.of(minecraftLivingEntity.getEquippedStack(slot));
	}
	
	public void setEquipment(EquipmentSlot slot, Object item) {
		minecraftLivingEntity.equipStack(slot, ItemStackJS.of(item).getItemStack());
	}
	
	public ItemStackJS getStackInHand(Hand hand) {
		return ItemStackJS.of(minecraftLivingEntity.getStackInHand(hand));
	}
	
	public void setHeldItem(Hand hand, Object item) {
		minecraftLivingEntity.setStackInHand(hand, ItemStackJS.of(item).getItemStack());
	}
	
	public ItemStackJS getMainHandItem() {
		return getStackInHand(Hand.MAIN_HAND);
	}
	
	public void setMainHandItem(Object item) {
		setHeldItem(Hand.MAIN_HAND, item);
	}
	
	public ItemStackJS getOffHandItem() {
		return getStackInHand(Hand.OFF_HAND);
	}
	
	public void setOffHandItem(Object item) {
		setHeldItem(Hand.OFF_HAND, item);
	}
	
	public void damageHeldItem(Hand hand, int amount, Consumer<ItemStackJS> onBroken) {
		ItemStack stack = minecraftLivingEntity.getStackInHand(hand);
		
		if (!stack.isEmpty()) {
			stack.damage(amount, minecraftLivingEntity, livingEntity -> onBroken.accept(ItemStackJS.of(stack)));
			
			if (stack.isEmpty()) {
				minecraftLivingEntity.setStackInHand(hand, ItemStack.EMPTY);
			}
		}
	}
	
	public void damageHeldItem(Hand hand, int amount) {
		damageHeldItem(hand, amount, stack -> {});
	}
	
	public void damageHeldItem() {
		damageHeldItem(Hand.MAIN_HAND, 1);
	}
	
	public boolean isHoldingInAnyHand(Object ingredient) {
		IngredientJS i = IngredientJS.of(ingredient);
		return i.testVanilla(minecraftLivingEntity.getStackInHand(Hand.MAIN_HAND)) || i.testVanilla(minecraftLivingEntity.getStackInHand(Hand.OFF_HAND));
	}
	
	public float getMovementSpeed() {
		return minecraftLivingEntity.getMovementSpeed();
	}
	
	public void setMovementSpeed(float speed) {
		minecraftLivingEntity.setMovementSpeed(speed);
	}
	
	public boolean canEntityBeSeen(EntityJS entity) {
		return minecraftLivingEntity.canSee(entity.minecraftEntity);
	}
	
	public float getAbsorptionAmount() {
		return minecraftLivingEntity.getAbsorptionAmount();
	}
	
	public void setAbsorptionAmount(float amount) {
		minecraftLivingEntity.setAbsorptionAmount(amount);
	}
	
	@Nullable
	public Map<String, Object> rayTrace() {
		return rayTrace(4);
	}
}