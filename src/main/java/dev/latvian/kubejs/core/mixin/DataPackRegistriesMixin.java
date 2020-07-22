package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.DataPackRegistriesHelper;
import dev.latvian.kubejs.core.DataPackRegistriesKJS;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ServerResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * @author LatvianModder
 */
@Mixin(ServerResourceManager.class)
public abstract class DataPackRegistriesMixin implements DataPackRegistriesKJS {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(CallbackInfo ci) {
		initKJS();
	}
	
	@ModifyArg(method = "reload", at = @At(value = "INVOKE", ordinal = 0,
	                                                      target = "Lnet/minecraft/resource/ReloadableResourceManager;beginReload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Ljava/util/concurrent/CompletableFuture;"),
	           index = 2)
	private static List<ResourcePack> resourcePackList(List<ResourcePack> list) {
		return DataPackRegistriesHelper.getResourcePackListKJS(list);
	}
}
