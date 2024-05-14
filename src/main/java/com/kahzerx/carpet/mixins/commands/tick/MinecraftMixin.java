package com.kahzerx.carpet.mixins.commands.tick;

import com.kahzerx.carpet.fakes.MinecraftTickRate;
import com.kahzerx.carpet.fakes.WorldTickRate;
import com.kahzerx.carpet.helpers.TickRateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Minecraft.class)
public class MinecraftMixin implements MinecraftTickRate {
	@Shadow
	public ClientWorld world;

	@Inject(method = "tick", at = @At("HEAD"))
	private void onClientTick(CallbackInfo ci) {
		if (this.world != null) {
			this.getTickRateManager().ifPresent(TickRateManager::tick);
			if (!this.getTickRateManager().map(TickRateManager::runsNormally).orElse(true)) {
//				CarpetClient.shapes.renewShapes();
			}
		}
	}

	@Override
	public Optional<TickRateManager> getTickRateManager() {
		if (this.world != null) {
			return Optional.of(((WorldTickRate) this.world).tickRateManager());
		}
		return Optional.empty();
	}
}
