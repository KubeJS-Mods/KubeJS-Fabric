package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

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
		minecraftLivingEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp);
	}
	
	public boolean isUndead() {
		return minecraftLivingEntity.isInvertedHealAndHarm();
	}
	
	public boolean isOnLadder() {
		return minecraftLivingEntity.onClimbable();
	}
	
	public boolean isSleeping() {
		return minecraftLivingEntity.isSleeping();
	}
	
	public boolean isElytraFlying() {
		return minecraftLivingEntity.isFallFlying();
	}
	
	@Nullable
	public LivingEntityJS getRevengeTarget() {
		return getWorld().getLivingEntity(minecraftLivingEntity.getLastHurtByMob());
	}
	
	public int getRevengeTimer() {
		return minecraftLivingEntity.getLastHurtByMobTimestamp();
	}
	
	public void setRevengeTarget(@Nullable LivingEntityJS target) {
		minecraftLivingEntity.setLastHurtByMob(target == null ? null : target.minecraftLivingEntity);
	}
	
	@Nullable
	public LivingEntityJS getLastAttackedEntity() {
		return getWorld().getLivingEntity(minecraftLivingEntity.getLastHurtMob());
	}
	
	public int getLastAttackedEntityTime() {
		return minecraftLivingEntity.getLastHurtMobTimestamp();
	}
	
	public int getIdleTime() {
		return minecraftLivingEntity.getNoActionTime();
	}
	
	public EntityPotionEffectsJS getPotionEffects() {
		return new EntityPotionEffectsJS(minecraftLivingEntity);
	}
	
	@Nullable
	public DamageSourceJS getLastDamageSource() {
		return minecraftLivingEntity.getLastDamageSource() == null ? null : new DamageSourceJS(getWorld(), minecraftLivingEntity.getLastDamageSource());
	}
	
	@Nullable
	public LivingEntityJS getAttackingEntity() {
		return getWorld().getLivingEntity(minecraftLivingEntity.getKillCredit());
	}
	
	public void swingArm(InteractionHand hand) {
		minecraftLivingEntity.swing(hand);
	}
	
	public ItemStackJS getEquipment(EquipmentSlot slot) {
		return ItemStackJS.of(minecraftLivingEntity.getItemBySlot(slot));
	}
	
	public void setEquipment(EquipmentSlot slot, Object item) {
		minecraftLivingEntity.setItemSlot(slot, ItemStackJS.of(item).getItemStack());
	}
	
	public ItemStackJS getStackInHand(InteractionHand hand) {
		return ItemStackJS.of(minecraftLivingEntity.getItemInHand(hand));
	}
	
	public void setHeldItem(InteractionHand hand, Object item) {
		minecraftLivingEntity.setItemInHand(hand, ItemStackJS.of(item).getItemStack());
	}
	
	public ItemStackJS getMainHandItem() {
		return getStackInHand(InteractionHand.MAIN_HAND);
	}
	
	public void setMainHandItem(Object item) {
		setHeldItem(InteractionHand.MAIN_HAND, item);
	}
	
	public ItemStackJS getOffHandItem() {
		return getStackInHand(InteractionHand.OFF_HAND);
	}
	
	public void setOffHandItem(Object item) {
		setHeldItem(InteractionHand.OFF_HAND, item);
	}
	
	public void damageHeldItem(InteractionHand hand, int amount, Consumer<ItemStackJS> onBroken) {
		ItemStack stack = minecraftLivingEntity.getItemInHand(hand);
		
		if (!stack.isEmpty()) {
			stack.hurtAndBreak(amount, minecraftLivingEntity, livingEntity -> onBroken.accept(ItemStackJS.of(stack)));
			
			if (stack.isEmpty()) {
				minecraftLivingEntity.setItemInHand(hand, ItemStack.EMPTY);
			}
		}
	}
	
	public void damageHeldItem(InteractionHand hand, int amount) {
		damageHeldItem(hand, amount, stack -> {});
	}
	
	public void damageHeldItem() {
		damageHeldItem(InteractionHand.MAIN_HAND, 1);
	}
	
	public boolean isHoldingInAnyHand(Object ingredient) {
		IngredientJS i = IngredientJS.of(ingredient);
		return i.testVanilla(minecraftLivingEntity.getItemInHand(InteractionHand.MAIN_HAND)) || i.testVanilla(minecraftLivingEntity.getItemInHand(InteractionHand.OFF_HAND));
	}
	
	public float getMovementSpeed() {
		return minecraftLivingEntity.getSpeed();
	}
	
	public void setMovementSpeed(float speed) {
		minecraftLivingEntity.setSpeed(speed);
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