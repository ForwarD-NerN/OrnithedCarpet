package com.kahzerx.carpet.mixins.commands.tick;

import com.kahzerx.carpet.fakes.MinecraftServerTickRate;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "pushAwayCollidingEntities", at = @At(value = "HEAD"), cancellable = true)
	private void onPushEntities(CallbackInfo ci) {
		if ((LivingEntity) (Object) this instanceof ServerPlayerEntity) {
			//#if MC>10809
			MinecraftServer server = ((LivingEntity) (Object) this).world.getServer();
			//#else
			//$$ MinecraftServer server = MinecraftServer.getInstance();
			//#endif
			if (((MinecraftServerTickRate) server).getTickRateManager().gameIsPaused()) {
				ci.cancel();
			}
		}
	}
}
