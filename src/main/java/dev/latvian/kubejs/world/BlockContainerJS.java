package dev.latvian.kubejs.world;

import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.ServerPlayerJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class BlockContainerJS {
	private static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");
	
	public final LevelAccessor minecraftWorld;
	private final BlockPos pos;
	
	private BlockState cachedState;
	private BlockEntity cachedEntity;
	
	public BlockContainerJS(LevelAccessor w, BlockPos p) {
		minecraftWorld = w;
		pos = p;
	}
	
	public void clearCache() {
		cachedState = null;
		cachedEntity = null;
	}
	
	public WorldJS getWorld() {
		return UtilsJS.getWorld((Level) minecraftWorld);
	}
	
	public BlockPos getPos() {
		return pos;
	}
	
	public String getDimension() {
		return ((Level) minecraftWorld).dimension().location().toString();
	}
	
	public int getX() {
		return getPos().getX();
	}
	
	public int getY() {
		return getPos().getY();
	}
	
	public int getZ() {
		return getPos().getZ();
	}
	
	public BlockContainerJS offset(Direction f, int d) {
		return new BlockContainerJS(minecraftWorld, getPos().relative(f, d));
	}
	
	public BlockContainerJS offset(Direction f) {
		return offset(f, 1);
	}
	
	public BlockContainerJS offset(int x, int y, int z) {
		return new BlockContainerJS(minecraftWorld, getPos().offset(x, y, z));
	}
	
	public BlockContainerJS getDown() {
		return offset(Direction.DOWN);
	}
	
	public BlockContainerJS getUp() {
		return offset(Direction.UP);
	}
	
	public BlockContainerJS getNorth() {
		return offset(Direction.NORTH);
	}
	
	public BlockContainerJS getSouth() {
		return offset(Direction.SOUTH);
	}
	
	public BlockContainerJS getWest() {
		return offset(Direction.WEST);
	}
	
	public BlockContainerJS getEast() {
		return offset(Direction.EAST);
	}
	
	@MinecraftClass
	public BlockState getBlockState() {
		if (cachedState == null) {
			cachedState = minecraftWorld.getBlockState(getPos());
		}
		
		return cachedState;
	}
	
	@MinecraftClass
	public void setBlockState(BlockState state, int flags) {
		minecraftWorld.setBlock(getPos(), state, flags);
		clearCache();
	}
	
	@ID
	public String getId() {
		return Registry.BLOCK.getKey(getBlockState().getBlock()).toString();
	}
	
	public void set(@ID String id, Map<?, ?> properties, int flags) {
		Block block = Registry.BLOCK.get(UtilsJS.getMCID(id));
		BlockState state = (block == null ? Blocks.AIR : block).defaultBlockState();
		
		if (!properties.isEmpty() && state.getBlock() != Blocks.AIR) {
			Map<String, Property> pmap = new HashMap<>();
			
			for (Property property : state.getProperties()) {
				pmap.put(property.getName(), property);
			}
			
			for (Map.Entry entry : properties.entrySet()) {
				Property<?> property = pmap.get(String.valueOf(entry.getKey()));
				
				if (property != null) {
					state = state.setValue(property, UtilsJS.cast(property.getValue(String.valueOf(entry.getValue())).get()));
				}
			}
		}
		
		setBlockState(state, flags);
	}
	
	public void set(@ID String id, Map<?, ?> properties) {
		set(id, properties, 3);
	}
	
	public void set(@ID String id) {
		set(id, Collections.emptyMap());
	}
	
	public Map<String, String> getProperties() {
		Map<String, String> map = new HashMap<>();
		BlockState state = getBlockState();
		
		for (Property property : state.getProperties()) {
			map.put(property.getName(), property.getName(state.getValue(property)));
		}
		
		return map;
	}
	
	@Nullable
	@MinecraftClass
	public BlockEntity getEntity() {
		if (cachedEntity == null || cachedEntity.isRemoved()) {
			cachedEntity = minecraftWorld.getBlockEntity(pos);
		}
		
		return cachedEntity;
	}
	
	@ID
	public String getEntityId() {
		BlockEntity entity = getEntity();
		return entity == null ? "minecraft:air" : Registry.BLOCK_ENTITY_TYPE.getKey(entity.getType()).toString();
	}
	
	@Nullable
	public MapJS getEntityData() {
		final BlockEntity entity = getEntity();
		
		if (entity != null) {
			MapJS entityData = MapJS.of(entity.save(new CompoundTag()));
			
			if (entityData != null) {
				entityData.changeListener = o -> entity.load(entity.getBlockState(), MapJS.nbt(o));
				return entityData;
			}
		}
		
		return null;
	}
	
	public int getLight() {
		return minecraftWorld.getMaxLocalRawBrightness(pos);
	}
	
	public boolean getCanSeeSky() {
		return minecraftWorld.canSeeSky(pos);
	}
	
	@Override
	public String toString() {
		String id = getId();
		Map<String, String> properties = getProperties();
		
		if (properties.isEmpty()) {
			return id;
		}
		
		StringBuilder builder = new StringBuilder(id);
		builder.append('[');
		
		boolean first = true;
		
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			if (first) {
				first = false;
			} else {
				builder.append(',');
			}
			
			builder.append(entry.getKey());
			builder.append('=');
			builder.append(entry.getValue());
		}
		
		builder.append(']');
		return builder.toString();
	}
	
	public ExplosionJS createExplosion() {
		return new ExplosionJS(minecraftWorld, getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D);
	}
	
	@Nullable
	public EntityJS createEntity(@ID String id) {
		EntityJS entity = getWorld().createEntity(id);
		
		if (entity != null) {
			entity.setPosition(this);
		}
		
		return entity;
	}
	
	public void spawnLightning(boolean effectOnly, @Nullable EntityJS player) {
		if (minecraftWorld instanceof ServerLevel) {
			LightningBolt e = EntityType.LIGHTNING_BOLT.create((ServerLevel) minecraftWorld);
			e.moveTo(new Vec3(getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D));
			e.setCause(player instanceof ServerPlayerJS ? ((ServerPlayerJS) player).minecraftPlayer : null);
			minecraftWorld.addFreshEntity(e);
		}
	}
	
	public void spawnLightning(boolean effectOnly) {
		spawnLightning(effectOnly, null);
	}
	
	public void spawnFireworks(FireworksJS fireworks) {
		if (minecraftWorld instanceof Level) {
			minecraftWorld.addFreshEntity(fireworks.createFireworkRocket((Level) minecraftWorld, getX() + 0.5D, getY() + 0.5D, getZ() + 0.5D));
		}
	}
	
	@Nullable
	public InventoryJS getInventory(Direction facing) {
		BlockEntity tileEntity = getEntity();
		
		// TODO
//		if (tileEntity != null)
//		{
//			IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing).orElse(null);
//
//			if (handler != null)
//			{
//				return new InventoryJS(handler);
//			}
//		}
		
		return null;
	}
	
	public MaterialJS getMaterial() {
		return MaterialListJS.INSTANCE.get(getBlockState().getMaterial());
	}
	
	@SuppressWarnings("deprecation")
	@Environment(EnvType.CLIENT)
	public ItemStackJS getItem() {
		BlockState state = getBlockState();
		return ItemStackJS.of(state.getBlock().getCloneItemStack(minecraftWorld, pos, state));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof CharSequence || obj instanceof ResourceLocation) {
			return getId().equals(obj.toString());
		}
		
		return super.equals(obj);
	}
}