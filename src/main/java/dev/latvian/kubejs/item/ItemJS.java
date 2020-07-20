package dev.latvian.kubejs.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.latvian.kubejs.text.Text;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ItemJS extends Item implements DynamicAttributeTool
{
	public final ItemBuilder properties;
	private final ImmutableMultimap<EntityAttribute, EntityAttributeModifier> attributes;
	private ItemStack containerItem;
	private Map<Identifier, Integer> toolsMap;
	private float miningSpeed;
	private Float attackDamage;
	private Float attackSpeed;

	public ItemJS(ItemBuilder p)
	{
		super(p.createItemProperties());
		properties = p;
		toolsMap = p.getToolsMap();
		miningSpeed = p.getMiningSpeed();
		attackDamage = p.getAttackDamage();
		attackSpeed = p.getAttackSpeed();

		ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		if (attackDamage != null)
			builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
		builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", (double) attackSpeed, EntityAttributeModifier.Operation.ADDITION));
		this.attributes = builder.build();
	}

	@Override
	public boolean hasGlint(ItemStack stack)
	{
		return properties.glow || super.hasGlint(stack);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<net.minecraft.text.Text> tooltip, TooltipContext flagIn)
	{
		super.appendTooltip(stack, worldIn, tooltip, flagIn);

		for (Text text : properties.tooltip)
		{
			tooltip.add(text.component());
		}
	}

	@Override
	public int getMiningLevel(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user)
	{
		if (tag instanceof Tag.Identified)
		{
			Tag.Identified<Item> identified = (Tag.Identified<Item>) tag;
			Integer level = toolsMap.get(identified.getId());
			if (level != null) return level;
		}
		return 0;
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state)
	{
		if (toolsMap.isEmpty()) return miningSpeed;
		return super.getMiningSpeedMultiplier(stack, state);
	}

	@Override
	public float getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user)
	{
		if (tag instanceof Tag.Identified)
		{
			Tag.Identified<Item> identified = (Tag.Identified<Item>) tag;
			Integer level = toolsMap.get(identified.getId());
			if (level != null) return miningSpeed;
		}
		return 1.0F;
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot)
	{
		return slot == EquipmentSlot.MAINHAND ? this.attributes : super.getAttributeModifiers(slot);
	}
}