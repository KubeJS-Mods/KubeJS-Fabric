package dev.latvian.kubejs.trade;

import dev.latvian.kubejs.core.TradeOfferKJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.MapJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

public class TradeRegistryEventJS extends EventJS {
	private static final Random RANDOM = new Random();

	public void regular(@ID String id, Integer level, VillagerTrades.ItemListing factory) {
		VillagerProfession profession = Registry.VILLAGER_PROFESSION.get(new ResourceLocation(id));
		VillagerTrades.TRADES.computeIfAbsent(profession, key -> new Int2ObjectOpenHashMap<>());
		Int2ObjectMap<VillagerTrades.ItemListing[]> map = VillagerTrades.TRADES.get(profession);

		map.put(level.intValue(), ArrayUtils.addAll(map.getOrDefault(level.intValue(), new VillagerTrades.ItemListing[0]), factory));
	}

	public void wandering(Integer level, VillagerTrades.ItemListing factory) {
		Int2ObjectMap<VillagerTrades.ItemListing[]> map = VillagerTrades.WANDERING_TRADER_TRADES;

		map.put(level.intValue(), ArrayUtils.addAll(map.getOrDefault(level.intValue(), new VillagerTrades.ItemListing[0]), factory));
	}

	public void replace(@ID String id, Integer level, Object from, Object with, boolean loose) {
		VillagerProfession profession = Registry.VILLAGER_PROFESSION.get(new ResourceLocation(id));
		VillagerTrades.TRADES.computeIfAbsent(profession, key -> new Int2ObjectOpenHashMap<>());
		Int2ObjectMap<VillagerTrades.ItemListing[]> map = VillagerTrades.TRADES.get(profession);
		VillagerTrades.ItemListing[] array = map.get(level.intValue());

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
			VillagerTrades.ItemListing factory = array[i];
			try {
				MerchantOffer existingOffer = factory.getOffer(null, RANDOM);
				TradeOfferKJS existingOfferKJS = (TradeOfferKJS) existingOffer;

				boolean replaceFirstBuy = ItemStack.matches(existingOfferKJS.getFirstBuyItem(), firstBuyItemFrom) && !existingOfferKJS.getFirstBuyItem().isEmpty() && !firstBuyItemFrom.isEmpty() && !firstBuyItemWith.isEmpty();
				boolean replaceSecondBuy = ItemStack.matches(existingOffer.getCostB(), secondBuyItemFrom) && !existingOffer.getCostB().isEmpty() && !secondBuyItemFrom.isEmpty() && !secondBuyItemWith.isEmpty();
				boolean replaceSell = ItemStack.matches(existingOffer.assemble(), sellItemFrom);
				boolean replaceUses = usesFrom != null && usesWith != null && existingOffer.getMaxUses() == usesFrom;
				boolean replaceExperience = experienceFrom != null && existingOffer.getXp() == experienceFrom;
				boolean replaceMultiplier = multiplierFrom != null && multiplierWith != null && existingOffer.getPriceMultiplier() == multiplierFrom;

				if (loose && (replaceFirstBuy || replaceSecondBuy || replaceSell || replaceUses || replaceExperience || replaceMultiplier) || !loose && (replaceFirstBuy && replaceSecondBuy && replaceSell && replaceUses && replaceExperience && replaceMultiplier)) {
					array[i] = (entity, random) -> {
						return new MerchantOffer(
								replaceFirstBuy ? firstBuyItemWith : existingOfferKJS.getFirstBuyItem(),
								replaceSecondBuy ? secondBuyItemWith : existingOffer.getCostB(),
								replaceSell ? sellItemWith : existingOffer.assemble(),
								replaceUses ? usesWith : existingOffer.getMaxUses(),
								replaceExperience ? experienceWith : existingOffer.getXp(),
								replaceMultiplier ? multiplierWith : existingOffer.getPriceMultiplier());
					};
				}
			} catch (Exception exception) {
				// It is likely that this is a NullPointerException which I cannot safely work around.
			}
		}

	}
}
