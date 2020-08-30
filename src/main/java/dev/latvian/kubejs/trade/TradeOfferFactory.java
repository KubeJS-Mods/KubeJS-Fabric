package dev.latvian.kubejs.trade;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;

import java.util.Random;

public class TradeOfferFactory implements VillagerTrades.ItemListing {
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
	public MerchantOffer getOffer(Entity entity, Random random) {
		return new MerchantOffer(new ItemStack(Items.EMERALD, this.price), new ItemStack(this.sell.getItem(), this.count), this.maxUses, this.experience, this.multiplier);
	}
}