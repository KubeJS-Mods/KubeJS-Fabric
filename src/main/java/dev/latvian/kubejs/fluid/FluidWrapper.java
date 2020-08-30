package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class FluidWrapper {
//	public FluidStackJS of(@ID Object o)
//	{
//		return FluidStackJS.of(o);
//	}
//
//	public FluidStackJS of(@ID Object o, Object amountOrNBT)
//	{
//		return FluidStackJS.of(o, amountOrNBT);
//	}
//
//	public FluidStackJS of(@ID Object o, int amount, Object nbt)
//	{
//		return FluidStackJS.of(o, amount, nbt);
//	}
	
	@MinecraftClass
	public Fluid getType(@ID String id) {
		Fluid f = Registry.FLUID.get(UtilsJS.getMCID(id));
		return f == null ? Fluids.EMPTY : f;
	}
	
	public List<String> getTypes() {
		List<String> set = new ArrayList<>();
		
		for (ResourceLocation id : Registry.FLUID.keySet()) {
			set.add(id.toString());
		}
		
		return set;
	}
}