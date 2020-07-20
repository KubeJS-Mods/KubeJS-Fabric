package dev.latvian.kubejs.block;

import dev.latvian.kubejs.docs.MinecraftClass;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

/**
 * @author LatvianModder
 */
public class MaterialJS {
	private final String id;
	private final Material minecraftMaterial;
	private final BlockSoundGroup sound;
	
	public MaterialJS(String i, Material m, BlockSoundGroup s) {
		id = i;
		minecraftMaterial = m;
		sound = s;
	}
	
	public String getId() {
		return id;
	}
	
	@MinecraftClass
	public Material getMinecraftMaterial() {
		return minecraftMaterial;
	}
	
	@MinecraftClass
	public BlockSoundGroup getSound() {
		return sound;
	}
}