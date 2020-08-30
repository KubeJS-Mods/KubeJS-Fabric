package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.util.TradeUtils;
import net.minecraft.world.entity.npc.VillagerTrades;

public class TradeWrapper {
	public VillagerTrades.ItemListing of(Object data) {
		return TradeUtils.of(data);
	}
}
