package dev.latvian.kubejs.fluid;

/**
 * @author LatvianModder
 */
public abstract class FluidStackJS {}
//public abstract class FluidStackJS implements WrappedJSObjectChangeListener<MapJS>
//{
//	public static FluidStackJS of(@Nullable Object o)
//	{
//		if (o == null)
//		{
//			return EmptyFluidStackJS.INSTANCE;
//		}
//		else if (o instanceof FluidStackJS)
//		{
//			return (FluidStackJS) o;
//		}
//		else if (o instanceof FluidStack)
//		{
//			return new BoundFluidStackJS((FluidStack) o);
//		}
//		else if (o instanceof Fluid)
//		{
//			return new UnboundFluidStackJS(((Fluid) o).getRegistryName());
//		}
//
//		MapJS map = MapJS.of(o);
//
//		if (map != null && map.containsKey("fluid"))
//		{
//			FluidStackJS stack = new UnboundFluidStackJS(new Identifier(map.get("fluid").toString()));
//
//			if (map.get("amount") instanceof Number)
//			{
//				stack.setAmount(((Number) map.get("amount")).intValue());
//			}
//
//			if (map.containsKey("nbt"))
//			{
//				stack.setNbt(map.get("nbt"));
//			}
//
//			return stack;
//		}
//
//		String[] s = o.toString().split(" ", 2);
//		return new UnboundFluidStackJS(new Identifier(s[0])).amount(UtilsJS.parseInt(s.length == 2 ? s[1] : "", FluidAttributes.BUCKET_VOLUME));
//	}
//
//	public static FluidStackJS of(@Nullable Object o, @Nullable Object amountOrNBT)
//	{
//		FluidStackJS stack = of(o);
//		Object n = UtilsJS.wrap(amountOrNBT, JSObjectType.ANY);
//
//		if (n instanceof Number)
//		{
//			stack.setAmount(((Number) n).intValue());
//		}
//		else if (n instanceof MapJS)
//		{
//			stack.setNbt(n);
//		}
//
//		return stack;
//	}
//
//	public static FluidStackJS of(@Nullable Object o, int amount, @Nullable Object nbt)
//	{
//		FluidStackJS stack = of(o);
//		stack.setAmount(amount);
//		stack.setNbt(nbt);
//		return stack;
//	}
//
//	public abstract String getId();
//
//	public Fluid getFluid()
//	{
//		Fluid f = Registry.FLUIDS.getValue(new Identifier(getId()));
//		return f == null ? Fluids.EMPTY : f;
//	}
//
//	public abstract FluidStack getFluidStack();
//
//	public boolean isEmpty()
//	{
//		return getAmount() <= 0;
//	}
//
//	public abstract int getAmount();
//
//	public abstract void setAmount(int amount);
//
//	public final FluidStackJS amount(int amount)
//	{
//		setAmount(amount);
//		return this;
//	}
//
//	@Nullable
//	public abstract MapJS getNbt();
//
//	public abstract void setNbt(@Nullable Object nbt);
//
//	public final FluidStackJS nbt(@Nullable Object nbt)
//	{
//		setNbt(nbt);
//		return this;
//	}
//
//	public abstract FluidStackJS copy();
//
//	@Override
//	public int hashCode()
//	{
//		return Objects.hash(getFluid(), getNbt());
//	}
//
//	public boolean equals(Object o)
//	{
//		FluidStackJS f = FluidStackJS.of(o);
//
//		if (f.isEmpty())
//		{
//			return false;
//		}
//
//		return getFluid() == f.getFluid() && Objects.equals(getNbt(), f.getNbt());
//	}
//
//	public boolean strongEquals(Object o)
//	{
//		FluidStackJS f = of(o);
//
//		if (f.isEmpty())
//		{
//			return false;
//		}
//
//		return getAmount() == f.getAmount() && getFluid() == f.getFluid() && Objects.equals(getNbt(), f.getNbt());
//	}
//
//	public String toString()
//	{
//		StringBuilder builder = new StringBuilder();
//
//		int amount = getAmount();
//		MapJS nbt = getNbt();
//
//		if (amount != FluidAttributes.BUCKET_VOLUME || nbt != null)
//		{
//			builder.append("fluid.of('");
//			builder.append(getId());
//			builder.append("')");
//
//			if (amount != FluidAttributes.BUCKET_VOLUME)
//			{
//				builder.append(".amount(");
//				builder.append(amount);
//				builder.append(')');
//			}
//
//			if (nbt != null)
//			{
//				builder.append(".nbt(");
//				nbt.toString(builder);
//				builder.append(')');
//			}
//		}
//		else
//		{
//			builder.append('\'');
//			builder.append(getId());
//			builder.append('\'');
//		}
//
//		return builder.toString();
//	}
//
//	@Override
//	public void onChanged(MapJS o)
//	{
//	}
//}