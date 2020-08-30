package dev.latvian.kubejs.util;

import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.trade.TradeOfferFactory;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class TradeUtils {
	public static VillagerTrades.ItemListing of(Object data) {
		MapJS mapJS = MapJS.of(data);

		ItemStack stack = ItemStackJS.of(mapJS.getOrDefault("stack", EmptyItemStackJS.INSTANCE)).getItemStack();
		Block block = (Block) mapJS.get("block");
		Item item = (Item) mapJS.get("item");
		Integer price = (Integer) mapJS.get("price");
		Integer count = (Integer) mapJS.get("count");
		Integer uses = (Integer) mapJS.get("uses");
		Integer experience = (Integer) mapJS.get("experience");
		Float multiplier = ((Number) mapJS.get("multiplier")).floatValue();

		price = price == null ? 1 : price;
		count = count == null ? 1 : count;
		uses = uses == null ? 1 : uses;
		experience = experience == null ? 8 : experience;
		multiplier = multiplier == null ? 1F : multiplier;

		if (!stack.isEmpty()) {
			return new TradeOfferFactory(stack, price, count, uses, experience, multiplier);
		} else if (item != null) {
			return new TradeOfferFactory(new ItemStack(item), price, count, uses, experience, multiplier);
		} else if (block != null) {
			return new TradeOfferFactory(new ItemStack(block), price, count, uses, experience, multiplier);
		} else {
			return null;
		}
	}
}
