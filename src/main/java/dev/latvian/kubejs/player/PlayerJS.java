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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public abstract class PlayerJS<E extends Player> extends LivingEntityJS implements WithAttachedData {
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
		minecraftPlayer.inventory.setChanged();
		minecraftPlayer.inventoryMenu.broadcastChanges();
	}
	
	public void give(Object item) {
		minecraftPlayer.inventory.add(ItemStackJS.of(item).getItemStack());
//		ItemHandlerHelper.giveItemToPlayer(minecraftPlayer, ItemStackJS.of(item).getItemStack());
	}
	
	public void giveInHand(Object item) {
		give(item);
		// TODO implement this
//		ItemHandlerHelper.giveItemToPlayer(minecraftPlayer, ItemStackJS.of(item).getItemStack(), getSelectedSlot());
	}
	
	public int getSelectedSlot() {
		return minecraftPlayer.inventory.selected;
	}
	
	public void setSelectedSlot(int index) {
		minecraftPlayer.inventory.selected = Mth.clamp(index, 0, 8);
	}
	
	public ItemStackJS getMouseItem() {
		return ItemStackJS.of(minecraftPlayer.inventory.getCarried());
	}
	
	public void setMouseItem(Object item) {
		minecraftPlayer.inventory.setCarried(ItemStackJS.of(item).getItemStack());
	}
	
	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
		super.setPositionAndRotation(x, y, z, yaw, pitch);
		
		if (minecraftPlayer instanceof ServerPlayer) {
			((ServerPlayer) minecraftPlayer).connection.teleport(x, y, z, yaw, pitch);
		}
	}
	
	@Override
	public void setStatusMessage(Object message) {
		minecraftPlayer.displayClientMessage(Text.of(message).component(), true);
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
		minecraftPlayer.getFoodData().eat(f, m);
	}
	
	public int getFoodLevel() {
		return minecraftPlayer.getFoodData().getFoodLevel();
	}
	
	public void setFoodLevel(int foodLevel) {
		minecraftPlayer.getFoodData().setFoodLevel(foodLevel);
	}
	
	public void addExhaustion(float exhaustion) {
		minecraftPlayer.causeFoodExhaustion(exhaustion);
	}
	
	public void addXP(int xp) {
		minecraftPlayer.giveExperiencePoints(xp);
	}
	
	public void addXPLevels(int l) {
		minecraftPlayer.giveExperienceLevels(l);
	}
	
	public void setXp(int xp) {
		minecraftPlayer.totalExperience = 0;
		minecraftPlayer.experienceProgress = 0F;
		minecraftPlayer.experienceLevel = 0;
		minecraftPlayer.giveExperiencePoints(xp);
	}
	
	public int getXp() {
		return minecraftPlayer.totalExperience;
	}
	
	public void setXpLevel(int l) {
		minecraftPlayer.totalExperience = 0;
		minecraftPlayer.experienceProgress = 0F;
		minecraftPlayer.experienceLevel = 0;
		minecraftPlayer.giveExperienceLevels(l);
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
			Vec3 v = minecraftPlayer.getLookAngle();
			double d0 = 1.5D;
			double d1 = 0.1D;
			Vec3 m = minecraftPlayer.getDeltaMovement();
			minecraftPlayer.setDeltaMovement(m.add(v.x * 0.1D + (v.x * 1.5D - m.x) * 0.5D, v.y * 0.1D + (v.y * 1.5D - m.y) * 0.5D, v.z * 0.1D + (v.z * 1.5D - m.z) * 0.5D));
		}
	}
	
	public void closeInventory() {
		minecraftPlayer.containerMenu = minecraftPlayer.inventoryMenu;
	}
	
	@MinecraftClass
	public AbstractContainerMenu getOpenInventory() {
		return minecraftPlayer.containerMenu;
	}
	
	public abstract boolean isMiningBlock();
}