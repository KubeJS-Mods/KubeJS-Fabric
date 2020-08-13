package dev.latvian.kubejs.block;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.BuilderBase;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BlockBuilder extends BuilderBase {
	public static BlockBuilder current;
	
	public MaterialJS material;
	public float hardness;
	public float resistance;
	public float lightLevel;
	public Identifier harvestTool;
	public int harvestLevel;
	public boolean opaque;
	public boolean fullBlock;
	public String renderType;
	public Int2IntOpenHashMap color;
	public final JsonObject textures;
	public String model;
	public BlockItemBuilder itemBuilder;
	public List<VoxelShape> customShape;
	public boolean notSolid;
	public boolean waterlogged;
	
	public BlockJS block;
	
	public BlockBuilder(String i) {
		super(i);
		material = MaterialListJS.INSTANCE.map.get("wood");
		hardness = 0.5F;
		resistance = -1F;
		lightLevel = 0F;
		harvestTool = null;
		harvestLevel = -1;
		opaque = true;
		fullBlock = false;
		renderType = "solid";
		color = new Int2IntOpenHashMap();
		color.defaultReturnValue(0xFFFFFFFF);
		textures = new JsonObject();
		texture(id.getNamespace() + ":block/" + id.getPath());
		model = id.getNamespace() + ":block/" + id.getPath();
		itemBuilder = new BlockItemBuilder(i);
		itemBuilder.blockBuilder = this;
		itemBuilder.parentModel = model;
		customShape = new ArrayList<>();
		notSolid = false;
		waterlogged = false;
	}
	
	@Override
	public String getType() {
		return "block";
	}
	
	public BlockBuilder material(MaterialJS m) {
		material = m;
		return this;
	}
	
	public BlockBuilder material(String id) {
		return material(MaterialListJS.INSTANCE.map.getOrDefault(id, material));
	}
	
	public BlockBuilder hardness(float h) {
		hardness = h;
		return this;
	}
	
	public BlockBuilder resistance(float r) {
		resistance = r;
		return this;
	}
	
	public BlockBuilder unbreakable() {
		hardness = -1F;
		resistance = Float.MAX_VALUE;
		return this;
	}
	
	public BlockBuilder lightLevel(float light) {
		lightLevel = light;
		return this;
	}
	
	public BlockBuilder harvestTool(Identifier tool, int level) {
		harvestTool = tool;
		harvestLevel = level;
		return this;
	}
	
	public BlockBuilder opaque(boolean o) {
		opaque = o;
		return this;
	}
	
	public BlockBuilder fullBlock(boolean f) {
		fullBlock = f;
		return this;
	}
	
	public BlockBuilder renderType(String l) {
		renderType = l;
		return this;
	}
	
	public BlockBuilder color(int index, int c) {
		color.put(index, 0xFF000000 | c);
		return this;
	}
	
	public BlockBuilder texture(String tex) {
		for (Direction direction : Direction.values()) {
			textures.addProperty(direction.getName(), tex);
		}
		
		textures.addProperty("particle", tex);
		return this;
	}
	
	public BlockBuilder texture(Direction direction, String tex) {
		textures.addProperty(direction.getName(), tex);
		return this;
	}
	
	public BlockBuilder model(String m) {
		model = m;
		itemBuilder.parentModel = model;
		return this;
	}
	
	public BlockBuilder item(@Nullable Consumer<BlockItemBuilder> i) {
		if (i == null) {
			itemBuilder = null;
		} else {
			i.accept(itemBuilder);
		}
		
		return this;
	}
	
	public BlockBuilder noItem() {
		return item(null);
	}
	
	public BlockBuilder shapeCube(double x0, double y0, double z0, double x1, double y1, double z1) {
		customShape.add(Block.createCuboidShape(x0, y0, z0, x1, y1, z1));
		return this;
	}
	
	public BlockBuilder notSolid() {
		notSolid = true;
		return this;
	}
	
	public BlockBuilder waterlogged() {
		waterlogged = true;
		return this;
	}
	
	public Block.Settings createProperties() {
		FabricBlockSettings properties = FabricBlockSettings.of(material.getMinecraftMaterial());
		properties.sounds(material.getSound());
		
		if (resistance >= 0F) {
			properties.strength(hardness, resistance);
		} else {
			properties.strength(hardness);
		}
		
		properties.lightLevel(state -> (int) (lightLevel * 15F));
		
		if (harvestTool != null) {
			properties.breakByTool(TagRegistry.item(harvestTool), Math.max(harvestLevel, 0));
		}
		
		if (notSolid) {
			properties.nonOpaque();
		}
		
		return properties;
	}
}