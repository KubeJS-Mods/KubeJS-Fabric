package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.util.BuilderBase;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Rarity;

/**
 * @author LatvianModder
 */
public class FluidBuilder extends BuilderBase {
	public String stillTexture;
	public String flowingTexture;
	private int color = 0xFFFFFFFF;
	private int luminosity = 0;
	private int density = 1000;
	private int temperature = 300;
	private int viscosity = 1000;
	private boolean isGaseous;
	private Rarity rarity = Rarity.COMMON;
	
	public Fluid stillFluid;
	public Fluid flowingFluid;
	public BucketItemJS bucketItem;
//	public FlowingFluidBlock block;
	
	public FluidBuilder(String i) {
		super(i);
		textureThin(0xFF0000);
	}
	
	@Override
	public String getType() {
		return "fluid";
	}
	
	public FluidBuilder color(int c) {
		color = c;
		
		if ((color & 0xFFFFFF) == color) {
			color |= 0xFF000000;
		}
		
		return this;
	}
	
	public FluidBuilder textureStill(@ID String id) {
		stillTexture = UtilsJS.getID(id);
		return this;
	}
	
	public FluidBuilder textureFlowing(@ID String id) {
		flowingTexture = UtilsJS.getID(id);
		return this;
	}
	
	public FluidBuilder textureThick(int color) {
		return textureStill(KubeJS.MOD_ID + ":fluid/fluid_thick").textureFlowing(KubeJS.MOD_ID + ":fluid/fluid_thick_flow").color(color);
	}
	
	public FluidBuilder textureThin(int color) {
		return textureStill(KubeJS.MOD_ID + ":fluid/fluid_thin").textureFlowing(KubeJS.MOD_ID + ":fluid/fluid_thin_flow").color(color);
	}
	
	public FluidBuilder luminosity(int luminosity) {
		this.luminosity = luminosity;
		return this;
	}
	
	public FluidBuilder density(int density) {
		this.density = density;
		return this;
	}
	
	public FluidBuilder temperature(int temperature) {
		this.temperature = temperature;
		return this;
	}
	
	public FluidBuilder viscosity(int viscosity) {
		this.viscosity = viscosity;
		return this;
	}
	
	public FluidBuilder gaseous() {
		isGaseous = true;
		return this;
	}
	
	public FluidBuilder rarity(Rarity rarity) {
		this.rarity = rarity;
		return this;
	}

//	public ForgeFlowingFluid.Properties createProperties()
//	{
//		FluidAttributes.Builder builder = FluidAttributes.builder(
//				new Identifier(stillTexture),
//				new Identifier(flowingTexture))
//				.translationKey("fluid." + id.getNamespace() + "." + id.getPath())
//				.color(color)
//				.rarity(rarity)
//				.density(density)
//				.viscosity(viscosity)
//				.luminosity(luminosity)
//				.temperature(temperature)
//				.rarity(rarity);
//
//		if (isGaseous)
//		{
//			builder.gaseous();
//		}
//
//		return new ForgeFlowingFluid.Properties(() -> stillFluid, () -> flowingFluid, builder).bucket(() -> bucketItem).block(() -> block);
//	}
}