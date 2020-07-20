package dev.latvian.kubejs.core.mixin;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
	@Shadow protected DefaultedList<ItemStack> inventory;

//	@Inject(locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"),
//	        method = "craftRecipe(Lnet/minecraft/recipe/Recipe;)V")
//	void onSmelt(Recipe<?> recipe, CallbackInfo ci, ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3) {
//		if (((BlockEntity) (Object) this).getWorld().isClient()) return;
//		
//		ItemSmeltCallback.EVENT.invoker().smelt((AbstractFurnaceBlockEntity) (Object) this, itemStack2.copy());
//	}
}
