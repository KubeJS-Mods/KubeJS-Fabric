package dev.latvian.kubejs.fluid;

/**
 * @author LatvianModder
 */
public class BoundFluidStackJS {}
//public class BoundFluidStackJS extends FluidStackJS
//{
//	private final FluidStack fluidStack;
//
//	public BoundFluidStackJS(FluidStack fs)
//	{
//		fluidStack = fs;
//	}
//
//	@Override
//	public String getId()
//	{
//		return fluidStack.getFluid().getRegistryName().toString();
//	}
//
//	@Override
//	public Fluid getFluid()
//	{
//		return fluidStack.getFluid();
//	}
//
//	@Override
//	public FluidStack getFluidStack()
//	{
//		return fluidStack;
//	}
//
//	@Override
//	public int getAmount()
//	{
//		return fluidStack.getAmount();
//	}
//
//	@Override
//	public void setAmount(int amount)
//	{
//		fluidStack.setAmount(amount);
//	}
//
//	@Override
//	@Nullable
//	public MapJS getNbt()
//	{
//		return MapJS.of(fluidStack.getTag());
//	}
//
//	@Override
//	public void setNbt(@Nullable Object nbt)
//	{
//		fluidStack.setTag(MapJS.nbt(nbt));
//	}
//
//	@Override
//	public FluidStackJS copy()
//	{
//		return new BoundFluidStackJS(fluidStack.copy());
//	}
//
//	@Override
//	public void onChanged(@Nullable MapJS o)
//	{
//		setNbt(o);
//	}
//}
