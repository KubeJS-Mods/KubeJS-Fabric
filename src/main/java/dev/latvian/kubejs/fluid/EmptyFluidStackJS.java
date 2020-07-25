package dev.latvian.kubejs.fluid;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

/**
 * @author LatvianModder
 */
public class EmptyFluidStackJS extends FluidStackJS {
	public static final EmptyFluidStackJS INSTANCE = new EmptyFluidStackJS();

	private EmptyFluidStackJS() {
		super(0, Fluids.EMPTY);
	}
}