package dev.latvian.kubejs.core.mixin;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.core.RecipeManagerKJS;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * @author LatvianModder
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin implements RecipeManagerKJS {
	@Inject(method = "apply", at = @At("HEAD"), cancellable = true)
	private void customRecipesHead(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
		customRecipesKJS(map);
		ci.cancel();
	}
	
	@Override
	@Accessor("recipes")
	public abstract void setRecipesKJS(Map<RecipeType<?>, Map<Identifier, Recipe<?>>> map);
}