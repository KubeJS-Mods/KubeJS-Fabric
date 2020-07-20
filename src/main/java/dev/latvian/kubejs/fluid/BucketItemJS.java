package dev.latvian.kubejs.fluid;

import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemGroup;

/**
 * @author LatvianModder
 */
public class BucketItemJS extends BucketItem {
	public final FluidBuilder properties;
	
	public BucketItemJS(FluidBuilder b) {
		super(b.stillFluid, new Settings().maxCount(1).group(ItemGroup.MISC));
		properties = b;
	}
}