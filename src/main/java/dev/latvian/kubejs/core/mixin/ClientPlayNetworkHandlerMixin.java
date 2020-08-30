package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.client.KubeJSClientEventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPacketListener.class)
@Environment(EnvType.CLIENT)
public class ClientPlayNetworkHandlerMixin {
	@Inject(method = "handleLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundLoginPacket;getPlayerId()I"))
	private void onGameJoinKJS(ClientboundLoginPacket packet, CallbackInfo ci) {
		KubeJSClientEventHandler.ON_JOIN.invoker().run();
	}
	
	@Inject(method = "handleRespawn", at = @At(value = "INVOKE",
	                                           target = "Lnet/minecraft/client/multiplayer/ClientLevel;addPlayer(ILnet/minecraft/client/player/AbstractClientPlayer;)V",
	                                           shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onPlayerRespawnKJS(ClientboundRespawnPacket packet, CallbackInfo ci, ResourceKey registryKey, DimensionType dimensionType, LocalPlayer clientPlayerEntity, int i, String string, LocalPlayer clientPlayerEntity2) {
		KubeJSClientEventHandler.ON_RESPAWN.invoker().accept(clientPlayerEntity2);
	}
}
