package com.kahzerx.carpet.mixins.commands.tick;

import com.kahzerx.carpet.fakes.MinecraftServerTickRate;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow
	public ServerPlayerEntity player;

	//#if MC>10809
	@Shadow
	private double firstGoodX;

	@Shadow
	private double firstGoodY;

	@Shadow
	private double firstGoodZ;
	//#endif

	@Inject(method = "handlePlayerInput", at = @At(value = "RETURN"))
	private void checkPlayerMoves(PlayerInputC2SPacket packet, CallbackInfo ci) {
		if (packet.getForwardSpeed() != 0.0F || packet.getSidewaysSpeed() != 0.0F || packet.getJumping() || packet.getSneaking()) {
			//#if MC>10809
			((MinecraftServerTickRate)player.getServer()).getTickRateManager().resetPlayerActivity();
			//#else
			//$$ ((MinecraftServerTickRate)player.server).getTickRateManager().resetPlayerActivity();
			//#endif
		}
	}

	//#if MC>10809
	private static long lastMovedTick = 0L;
	private static double lastMoved = 0.0D;
	//#endif

	@Inject(method = "handlePlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;isSleeping()Z", shift = At.Shift.BEFORE))
	private void checkPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
		//#if MC>10809
		double movedBy = player.getSquaredDistanceTo(firstGoodX, firstGoodY, firstGoodZ);
		if (movedBy == 0.0D) {
			return;
		}
		if (movedBy < 0.0009 && lastMoved > 0.0009 && Math.abs(
			//#if MC>10809
			player.getServer().getTicks()
			//#else
			//$$ player.server.getTicks()
			//#endif
			-lastMovedTick-20)<2) {
			return;
		}
		if (movedBy > 0.0D) {
			lastMoved = movedBy;
			//#if MC>10809
			lastMovedTick = player.getServer().getTicks();
			((MinecraftServerTickRate)player.getServer()).getTickRateManager().resetPlayerActivity();
			//#else
			//$$ lastMovedTick = player.server.getTicks();
			//$$ ((MinecraftServerTickRate)player.server).getTickRateManager().resetPlayerActivity();
			//#endif
		}
		//#endif
	}
}
