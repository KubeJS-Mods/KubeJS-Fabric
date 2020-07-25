package dev.latvian.kubejs.fluid;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.registry.Registry;

public class BoundFluidStackJS extends FluidStackJS {
	public BoundFluidStackJS(long amount, Fluid fluid) {
		super(amount, fluid);
	}
}
