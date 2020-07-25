package dev.latvian.kubejs.fluid;

import com.google.common.base.Objects;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.JsonSerializable;
import dev.latvian.kubejs.util.WrappedJS;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This class represents a quantity of a
 * specific fluid.
 * Fluids are internally stored as longs.
 *
 * @author vini2003
 */
public class FluidStackJS implements WrappedJS {
	final long amount;

	final Fluid fluid;

	public FluidStackJS(long amount, Fluid fluid) {
		this.amount = amount;
		this.fluid = fluid;
	}

	public static FluidStackJS of(Object o, int q) {
		if (o instanceof FluidStackJS) {
			return (FluidStackJS) o;
		} else if (o instanceof CharSequence ) {
			return new FluidStackJS(q, Registry.FLUID.get(new Identifier(((CharSequence) o).toString())));
		} else {
			return EmptyFluidStackJS.INSTANCE;
		}
	}

	public boolean isEmpty() {
		return this.amount == 0 || this.fluid == Fluids.EMPTY || this == EmptyFluidStackJS.INSTANCE;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FluidStackJS)) return false;
		FluidStackJS that = (FluidStackJS) o;
		return amount == that.amount && Objects.equal(fluid, that.fluid);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(amount, fluid);
	}
}
