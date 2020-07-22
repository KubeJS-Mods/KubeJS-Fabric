package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.callback.item.ItemSmeltCallback;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
	@Shadow protected DefaultedList<ItemStack> inventory;

	@Inject(locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0), method = "craftRecipe(Lnet/minecraft/recipe/Recipe;)V")
	void onSmelt(Recipe<?> recipe, CallbackInfo ci, ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3) {
		if (((BlockEntity) (Object) this).getWorld().isClient()) return;

		ItemSmeltCallback.EVENT.invoker().smelt((AbstractFurnaceBlockEntity) (Object) this, itemStack2.copy());
	}
}
