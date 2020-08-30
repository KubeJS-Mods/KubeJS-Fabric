package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.TradeOfferKJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MerchantOffer.class)
public class TradeOfferMixin implements TradeOfferKJS {
	@Shadow @Final private ItemStack baseCostA;
	
	@Override
	public ItemStack getFirstBuyItem() {
		return baseCostA;
	}
}
