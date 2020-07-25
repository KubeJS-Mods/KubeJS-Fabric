package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class FluidWrapper {
	public FluidStackJS of(@ID Object o, int amount) {
		return FluidStackJS.of(o, amount);
	}
	
	@MinecraftClass
	public Fluid getType(@ID String id) {
		Fluid f = Registry.FLUID.get(UtilsJS.getMCID(id));
		return f == null ? Fluids.EMPTY : f;
	}
	
	public List<String> getTypes() {
		List<String> set = new ArrayList<>();
		
		for (Identifier id : Registry.FLUID.getIds()) {
			set.add(id.toString());
		}
		
		return set;
	}
}