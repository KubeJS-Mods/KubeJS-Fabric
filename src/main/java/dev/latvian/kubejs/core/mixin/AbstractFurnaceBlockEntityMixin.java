package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.callback.item.ItemSmeltCallback;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
	@Inject(locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 0), method = "burn")
	void onSmelt(Recipe<?> recipe, CallbackInfo ci, ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3) {
		if (((BlockEntity) (Object) this).getLevel().isClientSide()) return;

		ItemSmeltCallback.EVENT.invoker().smelt((AbstractFurnaceBlockEntity) (Object) this, itemStack2.copy());
	}
}
