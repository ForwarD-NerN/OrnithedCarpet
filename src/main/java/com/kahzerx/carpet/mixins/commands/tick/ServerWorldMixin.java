package com.kahzerx.carpet.mixins.commands.tick;

import com.kahzerx.carpet.fakes.MinecraftServerTickRate;
import com.kahzerx.carpet.fakes.WorldTickRate;
import com.kahzerx.carpet.helpers.TickRateManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements WorldTickRate {
	@Shadow
	@Nullable
	public abstract MinecraftServer getServer();

	@Shadow
	protected abstract void doBlockEvents();

	@Shadow
	//#if MC>11202
	public abstract void doScheduledTicks();
	//#else
	//$$	public abstract boolean doScheduledTicks(boolean flush);
	//#endif

	@Override
	public TickRateManager tickRateManager() {
		return ((MinecraftServerTickRate) getServer()).getTickRateManager();
	}

	@Inject(method = "tickWeather", at = @At(value = "HEAD"), cancellable = true)
	public void onTickWeather(CallbackInfo ci) {
		if (!tickRateManager().runsNormally()) {
			ci.cancel();
		}
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldData;setTime(J)V"))
	private void onSetTime(WorldData worldData, long time) {
		if (tickRateManager().runsNormally()) {
			worldData.setTime(time);
		}
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldData;setTimeOfDay(J)V"))
	private void onSetTimeOfDay(WorldData worldData, long time) {
		if (tickRateManager().runsNormally()) {
			worldData.setTimeOfDay(time);
		}
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;doBlockEvents()V"))
	private void onBlockEvents(ServerWorld serverWorld) {
		if (tickRateManager().runsNormally()) {
			doBlockEvents();
		}
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target =
		//#if MC>11202
		"Lnet/minecraft/server/world/ServerWorld;doScheduledTicks()V"
		//#else
		//$$ "Lnet/minecraft/server/world/ServerWorld;doScheduledTicks(Z)Z"
		//#endif
	))
	//#if MC>11202
	private void onScheduledTicks(ServerWorld serverWorld) {
	//#else
	//$$ private boolean onScheduledTicks(ServerWorld instance, boolean flush) {
	//#endif
		if (tickRateManager().runsNormally()) {
			//#if MC>11202
			doScheduledTicks();
			//#else
			//$$ return doScheduledTicks(flush);
			//#endif
		}
		//#if MC<=11202
		//$$ return false;
		//#endif
	}
}
