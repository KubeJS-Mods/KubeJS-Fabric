package dev.latvian.kubejs.block;

import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class MaterialListJS
{
	public static final MaterialListJS INSTANCE = new MaterialListJS();

	public final Map<String, MaterialJS> map;
	public final MaterialJS air;

	private MaterialListJS()
	{
		map = new HashMap<>();
		air = add("air", Material.AIR, BlockSoundGroup.STONE);
		add("wood", Material.WOOD, BlockSoundGroup.WOOD);
		add("rock", Material.STONE, BlockSoundGroup.STONE);
		add("iron", Material.METAL, BlockSoundGroup.METAL);
		add("organic", Material.SOLID_ORGANIC, BlockSoundGroup.GRASS);
		add("earth", Material.SOIL, BlockSoundGroup.GRAVEL);
		add("water", Material.WATER, BlockSoundGroup.STONE);
		add("lava", Material.LAVA, BlockSoundGroup.STONE);
		add("leaves", Material.LEAVES, BlockSoundGroup.GRASS);
		add("plants", Material.PLANT, BlockSoundGroup.GRASS);
		add("sponge", Material.SPONGE, BlockSoundGroup.GRASS);
		add("wool", Material.WOOL, BlockSoundGroup.WOOL);
		add("sand", Material.AGGREGATE, BlockSoundGroup.SAND);
		add("glass", Material.GLASS, BlockSoundGroup.GLASS);
		add("tnt", Material.TNT, BlockSoundGroup.GRASS);
		add("coral", Material.UNUSED_PLANT, BlockSoundGroup.CORAL);
		add("ice", Material.ICE, BlockSoundGroup.GLASS);
		add("snow", Material.SNOW_LAYER, BlockSoundGroup.SNOW);
		add("clay", Material.ORGANIC_PRODUCT, BlockSoundGroup.GRAVEL);
		add("gourd", Material.GOURD, BlockSoundGroup.GRASS);
		add("dragon_egg", Material.EGG, BlockSoundGroup.STONE);
		add("portal", Material.PORTAL, BlockSoundGroup.STONE);
		add("cake", Material.CAKE, BlockSoundGroup.WOOL);
		add("web", Material.COBWEB, BlockSoundGroup.WOOL);
		add("slime", Material.ORGANIC_PRODUCT, BlockSoundGroup.SLIME);
		add("honey", Material.ORGANIC_PRODUCT, BlockSoundGroup.HONEY);
		add("berry_bush", Material.PLANT, BlockSoundGroup.SWEET_BERRY_BUSH);
		add("lantern", Material.METAL, BlockSoundGroup.LANTERN);
	}

	public MaterialJS add(MaterialJS m)
	{
		map.put(m.getId(), m);
		return m;
	}

	public MaterialJS add(String s, Material m, BlockSoundGroup e)
	{
		return add(new MaterialJS(s, m, e));
	}

	public MaterialJS get(String id)
	{
		MaterialJS m = map.get(id);
		return m == null ? air : m;
	}

	public MaterialJS get(Material minecraftMaterial)
	{
		for (MaterialJS materialJS : map.values())
		{
			if (materialJS.getMinecraftMaterial() == minecraftMaterial)
			{
				return materialJS;
			}
		}

		return air;
	}
}