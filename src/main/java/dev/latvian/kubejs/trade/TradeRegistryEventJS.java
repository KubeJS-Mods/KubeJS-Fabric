package dev.latvian.kubejs.trade;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.bindings.TradeWrapper;
import dev.latvian.kubejs.core.TradeOfferKJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.MapJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Random;

public class TradeRegistryEventJS extends EventJS {
	private static final Random RANDOM = new Random();

	public void regular(@ID String id, Integer level, TradeOffers.Factory factory) {
		VillagerProfession profession = Registry.VILLAGER_PROFESSION.get(new Identifier(id));
		TradeOffers.PROFESSION_TO_LEVELED_TRADE.computeIfAbsent(profession, key -> new Int2ObjectOpenHashMap<>());
		Int2ObjectMap<TradeOffers.Factory[]> map = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(profession);

		map.put(level.intValue(), ArrayUtils.addAll(map.getOrDefault(level.intValue(), new TradeOffers.Factory[0]), factory));
	}

	public void replace(@ID String id, Integer level, Object from, Object with, boolean loose) {
		VillagerProfession profession = Registry.VILLAGER_PROFESSION.get(new Identifier(id));
		TradeOffers.PROFESSION_TO_LEVELED_TRADE.computeIfAbsent(profession, key -> new Int2ObjectOpenHashMap<>());
		Int2ObjectMap<TradeOffers.Factory[]> map = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(profession);
		TradeOffers.Factory[] array = map.get(level.intValue());

		MapJS fromMap = MapJS.of(from);
		MapJS withMap = MapJS.of(with);

		ItemStack firstBuyItemFrom = ItemStackJS.of(fromMap.getOrDefault("firstBuy", EmptyItemStackJS.INSTANCE)).getItemStack();
		ItemStack secondBuyItemFrom = ItemStackJS.of(fromMap.getOrDefault("secondBuy", EmptyItemStackJS.INSTANCE)).getItemStack();
		ItemStack sellItemFrom = ItemStackJS.of(fromMap.getOrDefault("sell", EmptyItemStackJS.INSTANCE)).getItemStack();

		ItemStack firstBuyItemWith = ItemStackJS.of(withMap.getOrDefault("firstBuy", EmptyItemStackJS.INSTANCE)).getItemStack();
		ItemStack secondBuyItemWith = ItemStackJS.of(withMap.getOrDefault("secondBuy", EmptyItemStackJS.INSTANCE)).getItemStack();
		ItemStack sellItemWith = ItemStackJS.of(withMap.getOrDefault("sell", EmptyItemStackJS.INSTANCE)).getItemStack();

		Integer usesFrom = (Integer) fromMap.get("uses");
		Integer usesWith = (Integer) withMap.get("uses");

		Integer experienceFrom = (Integer) fromMap.get("experience");
		Integer experienceWith = (Integer) withMap.get("experience");

		Float multiplierFrom = (Float) fromMap.get("multiplier");
		Float multiplierWith = (Float) withMap.get("multiplier");

		for (int i = 0; i < array.length; ++i) {
			TradeOffers.Factory factory = array[i];
			try {
				TradeOffer existingOffer = factory.create(null, RANDOM);
				TradeOfferKJS existingOfferKJS = (TradeOfferKJS) existingOffer;

				boolean replaceFirstBuy = ItemStack.areEqual(existingOfferKJS.getFirstBuyItem(), firstBuyItemFrom) && !existingOfferKJS.getFirstBuyItem().isEmpty() && !firstBuyItemFrom.isEmpty() && !firstBuyItemWith.isEmpty();
				boolean replaceSecondBuy = ItemStack.areEqual(existingOffer.getSecondBuyItem(), secondBuyItemFrom) && !existingOffer.getSecondBuyItem().isEmpty() && !secondBuyItemFrom.isEmpty() && !secondBuyItemWith.isEmpty();
				boolean replaceSell = ItemStack.areEqual(existingOffer.getSellItem(), sellItemFrom);
				boolean replaceUses = usesFrom != null && usesWith != null && existingOffer.getMaxUses() == usesFrom;
				boolean replaceExperience = experienceFrom != null && existingOffer.getTraderExperience() == experienceFrom;
				boolean replaceMultiplier = multiplierFrom != null && multiplierWith != null && existingOffer.getPriceMultiplier() == multiplierFrom;

				if (loose && (replaceFirstBuy || replaceSecondBuy || replaceSell || replaceUses || replaceExperience || replaceMultiplier) || !loose && (replaceFirstBuy && replaceSecondBuy && replaceSell && replaceUses && replaceExperience && replaceMultiplier)) {
					array[i] = (entity, random) -> {
						return new TradeOffer(
								replaceFirstBuy ? firstBuyItemWith : existingOfferKJS.getFirstBuyItem(),
								replaceSecondBuy ? secondBuyItemWith : existingOffer.getSecondBuyItem(),
								replaceSell ? sellItemWith : existingOffer.getSellItem(),
								replaceUses ? usesWith : existingOffer.getMaxUses(),
								replaceExperience ? experienceWith : existingOffer.getTraderExperience(),
								replaceMultiplier ? multiplierWith : existingOffer.getPriceMultiplier());
					};
				}
			} catch (Exception exception) {
				// It is likely that this is a NullPointerException which I cannot safely work around.
			}
		}

	}
}
