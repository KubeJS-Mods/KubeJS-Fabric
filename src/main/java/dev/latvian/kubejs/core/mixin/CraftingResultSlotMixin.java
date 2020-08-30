package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.callback.item.ItemCraftCallback;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public class CraftingResultSlotMixin {
	@Shadow @Final private Player player;
	
	@Shadow @Final private CraftingContainer craftSlots;
	
	@Inject(at = @At("HEAD"), method = "checkTakeAchievements")
	void onCraftKJS(ItemStack stack, CallbackInfo ci) {
		ItemCraftCallback.EVENT.invoker().craft(this.player, craftSlots, stack);
	}
}