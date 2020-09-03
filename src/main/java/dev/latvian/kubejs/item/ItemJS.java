package dev.latvian.kubejs.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.latvian.kubejs.text.Text;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ItemJS extends Item implements DynamicAttributeTool {
	public final ItemBuilder properties;
	private final ImmutableMultimap<Attribute, AttributeModifier> attributes;
	private ItemStack containerItem;
	private Map<ResourceLocation, Integer> toolsMap;
	private float miningSpeed;
	private Float attackDamage;
	private Float attackSpeed;
	
	public ItemJS(ItemBuilder p) {
		super(p.createItemProperties());
		properties = p;
		toolsMap = p.getToolsMap();
		miningSpeed = p.getMiningSpeed();
		attackDamage = p.getAttackDamage();
		attackSpeed = p.getAttackSpeed();
		
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		if (attackDamage != null)
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
		if (attackSpeed != null)
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double) attackSpeed, AttributeModifier.Operation.ADDITION));
		this.attributes = builder.build();
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return properties.glow || super.isFoil(stack);
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		
		for (Text text : properties.tooltip) {
			tooltip.add(text.component());
		}
	}
	
	@Override
	public int getMiningLevel(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		if (tag instanceof Tag.Named) {
			Tag.Named<Item> identified = (Tag.Named<Item>) tag;
			Integer level = toolsMap.get(identified.getName());
			if (level != null) return level;
		}
		return 0;
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (toolsMap.isEmpty()) return miningSpeed;
		return super.getDestroySpeed(stack, state);
	}
	
	@Override
	public float getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		if (tag instanceof Tag.Named) {
			Tag.Named<Item> identified = (Tag.Named<Item>) tag;
			Integer level = toolsMap.get(identified.getName());
			if (level != null) return miningSpeed;
		}
		return 1.0F;
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? this.attributes : super.getDefaultAttributeModifiers(slot);
	}
}