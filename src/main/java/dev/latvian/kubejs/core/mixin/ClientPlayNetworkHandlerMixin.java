package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.client.KubeJSClientEventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
@Environment(EnvType.CLIENT)
public class ClientPlayNetworkHandlerMixin {
	@Inject(method = "onGameJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/GameJoinS2CPacket;getEntityId()I"))
	private void onGameJoinKJS(GameJoinS2CPacket packet, CallbackInfo ci) {
		KubeJSClientEventHandler.ON_JOIN.invoker().run();
	}
	
	@Inject(method = "onPlayerRespawn", at = @At(value = "INVOKE",
	                                             target = "Lnet/minecraft/client/world/ClientWorld;addPlayer(ILnet/minecraft/client/network/AbstractClientPlayerEntity;)V",
	                                             shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onPlayerRespawnKJS(PlayerRespawnS2CPacket packet, CallbackInfo ci, RegistryKey registryKey, RegistryKey registryKey2, DimensionType dimensionType, ClientPlayerEntity clientPlayerEntity, int i, String string, ClientPlayerEntity clientPlayerEntity2) {
		KubeJSClientEventHandler.ON_RESPAWN.invoker().accept(clientPlayerEntity2);
	}
}
