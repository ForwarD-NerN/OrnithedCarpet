package com.kahzerx.carpet.mixins.commands.tick;

import com.kahzerx.carpet.fakes.WorldTickRate;
import com.kahzerx.carpet.helpers.TickRateManager;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.Difficulty;
import net.minecraft.world.WorldSettings;
//#if MC>11202
import net.minecraft.world.dimension.DimensionType;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements WorldTickRate {
	private TickRateManager tickRateManager;
	@Override
	public TickRateManager tickRateManager() {
		return tickRateManager;
	}

	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInit(ClientPlayNetworkHandler clientPlayNetworkHandler, WorldSettings worldSettings,
						//#if MC>11202
						DimensionType dimensionType,
						//#else
						//$$ int dimension,
						//#endif
						Difficulty difficulty, Profiler profiler, CallbackInfo ci) {
		this.tickRateManager = new TickRateManager();
	}
}
