package dev.latvian.kubejs.core.mixin;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import dev.latvian.kubejs.core.ResourcePackManagerKJS;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin implements ResourcePackManagerKJS {
	@Mutable @Shadow @Final private Set<ResourcePackProvider> providers;
	
	@Override
	public void addProviderKJS(ResourcePackProvider provider) {
		LinkedHashSet<ResourcePackProvider> set = Sets.newLinkedHashSet(providers);
		set.add(provider);
		providers = ImmutableSet.copyOf(set);
	}
}
