package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.TradeOfferKJS;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TradeOffer.class)
public class TradeOfferMixin implements TradeOfferKJS {
	@Shadow @Final private ItemStack firstBuyItem;

	@Override
	public ItemStack getFirstBuyItem() {
		return firstBuyItem;
	}
}
