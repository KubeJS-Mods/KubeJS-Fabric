package dev.latvian.kubejs.player;

import com.mojang.authlib.GameProfile;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.entity.LivingEntityJS;
import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.AttachedData;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.util.WithAttachedData;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public abstract class PlayerJS<E extends PlayerEntity> extends LivingEntityJS implements WithAttachedData {
	@MinecraftClass
	public final E minecraftPlayer;
	
	private final PlayerDataJS playerData;
	private InventoryJS inventory;
	
	public PlayerJS(PlayerDataJS d, WorldJS w, E p) {
		super(w, p);
		playerData = d;
		minecraftPlayer = p;
	}
	
	@Override
	public AttachedData getData() {
		return playerData.getData();
	}
	
	@Override
	public boolean isPlayer() {
		return true;
	}

//	public boolean isFake()
//	{
//		return minecraftPlayer instanceof FakePlayer;
//	}
	
	public String toString() {
		return minecraftPlayer.getGameProfile().getName();
	}
	
	@Override
	public GameProfile getProfile() {
		return minecraftPlayer.getGameProfile();
	}
	
	public InventoryJS getInventory() {
		if (inventory == null) {
			inventory = new InventoryJS(minecraftPlayer.inventory) {
				@Override
				public void markDirty() {
					sendInventoryUpdate();
				}
			};
		}
		
		return inventory;
	}
	
	public void sendInventoryUpdate() {
		minecraftPlayer.inventory.markDirty();
		minecraftPlayer.playerScreenHandler.sendContentUpdates();
	}
	
	public void give(Object item) {
		minecraftPlayer.inventory.insertStack(ItemStackJS.of(item).getItemStack());
//		ItemHandlerHelper.giveItemToPlayer(minecraftPlayer, ItemStackJS.of(item).getItemStack());
	}
	
	public void giveInHand(Object item) {
		give(item);
		// TODO implement this
//		ItemHandlerHelper.giveItemToPlayer(minecraftPlayer, ItemStackJS.of(item).getItemStack(), getSelectedSlot());
	}
	
	public int getSelectedSlot() {
		return minecraftPlayer.inventory.selectedSlot;
	}
	
	public void setSelectedSlot(int index) {
		minecraftPlayer.inventory.selectedSlot = MathHelper.clamp(index, 0, 8);
	}
	
	public ItemStackJS getMouseItem() {
		return ItemStackJS.of(minecraftPlayer.inventory.getCursorStack());
	}
	
	public void setMouseItem(Object item) {
		minecraftPlayer.inventory.setCursorStack(ItemStackJS.of(item).getItemStack());
	}
	
	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
		super.setPositionAndRotation(x, y, z, yaw, pitch);
		
		if (minecraftPlayer instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) minecraftPlayer).networkHandler.requestTeleport(x, y, z, yaw, pitch);
		}
	}
	
	@Override
	public void setStatusMessage(Object message) {
		minecraftPlayer.sendMessage(Text.of(message).component(), true);
	}
	
	public boolean isCreativeMode() {
		return minecraftPlayer.isCreative();
	}
	
	public boolean isSpectator() {
		return minecraftPlayer.isSpectator();
	}
	
	public abstract PlayerStatsJS getStats();
	
	@Override
	public void spawn() {
	}

	/*
	@Override
	public MapJS getNbt()
	{
		CompoundTag nbt = minecraftEntity.getPersistentData();
		CompoundTag nbt1 = (CompoundTag) nbt.get(PlayerEntity.PERSISTED_NBT_TAG);
		MapJS map = MapJS.of(nbt1 == null ? null : nbt1.get("KubeJS"));

		if (map == null)
		{
			map = new MapJS();
		}

		map.changeListener = m -> {
			CompoundTag n = MapJS.nbt(m);

			if (n != null)
			{
				CompoundTag n1 = minecraftEntity.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
				n1.put("KubeJS", n);
				minecraftEntity.getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, n1);
			}
		};

		return map;
	}*/
	
	public void sendData(String channel, @Nullable Object data) {
	}
	
	public void addFood(int f, float m) {
		minecraftPlayer.getHungerManager().add(f, m);
	}
	
	public int getFoodLevel() {
		return minecraftPlayer.getHungerManager().getFoodLevel();
	}
	
	public void setFoodLevel(int foodLevel) {
		minecraftPlayer.getHungerManager().setFoodLevel(foodLevel);
	}
	
	public void addExhaustion(float exhaustion) {
		minecraftPlayer.addExhaustion(exhaustion);
	}
	
	public void addXP(int xp) {
		minecraftPlayer.addExperience(xp);
	}
	
	public void addXPLevels(int l) {
		minecraftPlayer.addExperienceLevels(l);
	}
	
	public void setXp(int xp) {
		minecraftPlayer.totalExperience = 0;
		minecraftPlayer.experienceProgress = 0F;
		minecraftPlayer.experienceLevel = 0;
		minecraftPlayer.addExperience(xp);
	}
	
	public int getXp() {
		return minecraftPlayer.totalExperience;
	}
	
	public void setXpLevel(int l) {
		minecraftPlayer.totalExperience = 0;
		minecraftPlayer.experienceProgress = 0F;
		minecraftPlayer.experienceLevel = 0;
		minecraftPlayer.addExperienceLevels(l);
	}
	
	public int getXpLevel() {
		return minecraftPlayer.experienceLevel;
	}
	
	public abstract void openOverlay(Overlay overlay);
	
	public abstract void closeOverlay(String overlay);
	
	public void closeOverlay(Overlay overlay) {
		closeOverlay(overlay.id);
	}
	
	public void boostElytraFlight() {
		if (minecraftPlayer.isFallFlying()) {
			Vec3d v = minecraftPlayer.getRotationVector();
			double d0 = 1.5D;
			double d1 = 0.1D;
			Vec3d m = minecraftPlayer.getVelocity();
			minecraftPlayer.setVelocity(m.add(v.x * 0.1D + (v.x * 1.5D - m.x) * 0.5D, v.y * 0.1D + (v.y * 1.5D - m.y) * 0.5D, v.z * 0.1D + (v.z * 1.5D - m.z) * 0.5D));
		}
	}
	
	public void closeInventory() {
		minecraftPlayer.currentScreenHandler = minecraftPlayer.playerScreenHandler;
	}
	
	@MinecraftClass
	public ScreenHandler getOpenInventory() {
		return minecraftPlayer.currentScreenHandler;
	}
	
	public abstract boolean isMiningBlock();
}