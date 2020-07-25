package dev.latvian.kubejs.trade;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;

import java.util.Random;

public class TradeOfferFactory implements TradeOffers.Factory {
	private final ItemStack sell;
	private final int price;
	private final int count;
	private final int maxUses;
	private final int experience;
	private final float multiplier;

	public TradeOfferFactory(Block block, int price, int count, int maxUses, int experience) {
		this(new ItemStack(block), price, count, maxUses, experience);
	}

	public TradeOfferFactory(Item item, int price, int count, int maxUses, int experience) {
		this(new ItemStack(item), price, count, maxUses, experience);
	}

	public TradeOfferFactory(ItemStack stack, int price, int count, int maxUses, int experience) {
		this(stack, price, count, maxUses, experience, 0.05F);
	}

	public TradeOfferFactory(ItemStack stack, int price, int count, int maxUses, int experience, float multiplier) {
		this.sell = stack;
		this.price = price;
		this.count = count;
		this.maxUses = maxUses;
		this.experience = experience;
		this.multiplier = multiplier;
	}

	@Override
	public TradeOffer create(Entity entity, Random random) {
		return new TradeOffer(new ItemStack(Items.EMERALD, this.price), new ItemStack(this.sell.getItem(), this.count), this.maxUses, this.experience, this.multiplier);
	}
}