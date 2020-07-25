package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.trade.TradeOfferFactory;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.TradeUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.apache.commons.lang3.ArrayUtils;

public class TradeWrapper {
	public TradeOffers.Factory of(Object data) {
		return TradeUtils.of(data);
	}
}
