package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.callback.item.ItemCraftCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {
	@Shadow @Final private PlayerEntity player;

	@Shadow @Final private CraftingInventory input;

	@Inject(at = @At("HEAD"), method = "onCrafted(Lnet/minecraft/item/ItemStack;)V")
	void onCraft(ItemStack stack, CallbackInfo ci) {
		ItemCraftCallback.EVENT.invoker().craft(this.player, input, stack);
	}
}