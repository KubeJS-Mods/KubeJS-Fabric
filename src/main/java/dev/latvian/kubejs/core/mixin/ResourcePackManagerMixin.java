package dev.latvian.kubejs.core.mixin;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import dev.latvian.kubejs.core.ResourcePackManagerKJS;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(PackRepository.class)
public class ResourcePackManagerMixin implements ResourcePackManagerKJS {
	@Mutable @Shadow @Final private Set<RepositorySource> sources;
	
	@Override
	public void addProviderKJS(RepositorySource provider) {
		LinkedHashSet<RepositorySource> set = Sets.newLinkedHashSet(sources);
		set.add(provider);
		sources = ImmutableSet.copyOf(set);
	}
}
