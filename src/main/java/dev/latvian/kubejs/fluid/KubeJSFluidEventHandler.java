package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.core.AfterScriptLoadCallback;

/**
 * @author LatvianModder
 */
public class KubeJSFluidEventHandler
{
	public void init()
	{
		AfterScriptLoadCallback.EVENT.register(this::registry);
	}

	private void registry()
	{
		// TODO
//		for (FluidBuilder builder : KubeJSObjects.FLUIDS.values())
//		{
//			builder.stillFluid = new ForgeFlowingFluid.Source(builder.createProperties());
//			builder.stillFluid.setRegistryName(builder.id);
//			event.getRegistry().register(builder.stillFluid);
//
//			builder.flowingFluid = new ForgeFlowingFluid.Flowing(builder.createProperties());
//			builder.flowingFluid.setRegistryName(builder.id.getNamespace() + ":flowing_" + builder.id.getPath());
//			event.getRegistry().register(builder.flowingFluid);
//		}
	}
}