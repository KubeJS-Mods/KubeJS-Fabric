package dev.latvian.kubejs.trade;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.MapJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;

public class TradeRegistryEventJS extends EventJS {
	public void regular(@ID String id, Object data) {
		MapJS mapJS = MapJS.of(data);

		Integer level = (Integer) mapJS.get("level");
		ItemStack stack = ItemStackJS.of(mapJS.get("stack")).getItemStack();
		Block block = (Block) mapJS.get("block");
		Item item = (Item) mapJS.get("item");
		Integer price = (Integer) mapJS.get("price");
		Integer count = (Integer) mapJS.get("count");
		Integer uses = (Integer) mapJS.get("uses");
		Integer experience = (Integer) mapJS.get("experience");
		Float multiplier = ((Number) mapJS.get("multiplier")).floatValue();

		level = level == null ? 1 : level;
		price = price == null ? 1 : price;
		count = count == null ? 1 : count;
		uses = uses == null ? 1 : uses;
		experience = experience == null ? 8 : experience;
		multiplier = multiplier == null ? 1F : multiplier;

		VillagerProfession profession = Registry.VILLAGER_PROFESSION.get(new Identifier(id));
		TradeOffers.PROFESSION_TO_LEVELED_TRADE.computeIfAbsent(profession, key -> new Int2ObjectOpenHashMap<>());
		Int2ObjectMap<TradeOffers.Factory[]> map = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(profession);

		if (!stack.isEmpty()) {
			map.put(level.intValue(), ArrayUtils.addAll(map.getOrDefault(level.intValue(), new TradeOffers.Factory[0]), new TradeOfferFactory(stack, price, count, uses, experience, multiplier)));
 		} else if (item != null) {
			map.put(level.intValue(), ArrayUtils.addAll(map.getOrDefault(level.intValue(), new TradeOffers.Factory[0]), new TradeOfferFactory(new ItemStack(item), price, count, uses, experience, multiplier)));
		} else if (block != null) {
			map.put(level.intValue(), ArrayUtils.addAll(map.getOrDefault(level.intValue(), new TradeOffers.Factory[0]), new TradeOfferFactory(new ItemStack(block), price, count, uses, experience, multiplier)));
		}
	}
}
