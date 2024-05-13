package com.kahzerx.carpet.mixins.commands.tick;

import com.kahzerx.carpet.fakes.MinecraftServerTickRate;
import com.kahzerx.carpet.helpers.ServerTickRateManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.chunk.ServerChunkCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {
	@Final
	@Shadow
	private ServerWorld world;

	@Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
	private void onTick(BooleanSupplier hasTimeLeft, CallbackInfoReturnable<Boolean> cir) {
		ServerTickRateManager strm = ((MinecraftServerTickRate) world.getServer()).getTickRateManager();
		if (!strm.runsNormally() && strm.deeplyFrozen()) {
			cir.setReturnValue(false);
		}

	}
}
