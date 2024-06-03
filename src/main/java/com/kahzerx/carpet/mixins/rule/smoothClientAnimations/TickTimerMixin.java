package com.kahzerx.carpet.mixins.rule.smoothClientAnimations;

import com.kahzerx.carpet.CarpetSettings;
import com.kahzerx.carpet.fakes.MinecraftTickRate;
import com.kahzerx.carpet.helpers.TickRateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.TickTimer;
import org.spongepowered.asm.mixin.Mixin;
//#if MC<=11102
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.injection.Redirect;
//#endif
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(TickTimer.class)
public class TickTimerMixin {
	//#if MC<=11102
	//$$	@Shadow
	//$$	float tps;
	//#endif
	//#if MC<=11102
	//$$ @Inject(method = "advance", at = @At(value = "HEAD"))
	//$$ private void onAdvance(CallbackInfo ci) {
	//$$ 	if (CarpetSettings.smoothClientAnimations) {
	//$$ 		Optional<TickRateManager> trm = ((MinecraftTickRate) Minecraft.getInstance()).getTickRateManager();
	//$$ 		if (trm.isPresent() && trm.get().runsNormally()) {
	//$$ 			this.tps = 3.0f;
	//$$ 		}
	//$$ 	}
	//$$ }
	//#endif

	//#if MC>11102
	@Redirect(method = "advance", at = @At(value = "FIELD", target = "Lnet/minecraft/client/TickTimer;mspt:F"))
	private float adjustSpeed(TickTimer instance) {
		if (CarpetSettings.smoothClientAnimations) {
			Optional<TickRateManager> trm = ((MinecraftTickRate) Minecraft.getInstance()).getTickRateManager();
			if (trm.isPresent() && trm.get().runsNormally()) {
				return Math.max(50.0f, trm.get().mspt());
			}
		}
		return 50f;
	}
	//#endif
}
