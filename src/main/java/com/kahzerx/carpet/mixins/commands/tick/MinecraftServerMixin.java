package com.kahzerx.carpet.mixins.commands.tick;

import com.kahzerx.carpet.fakes.MinecraftServerTickRate;
import com.kahzerx.carpet.helpers.ServerTickRateManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements MinecraftServerTickRate {
	private ServerTickRateManager serverTickRateManager;
	@Override
	public ServerTickRateManager getTickRateManager() {
		return serverTickRateManager;
	}

	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInit(CallbackInfo ci) {
		this.serverTickRateManager = new ServerTickRateManager((MinecraftServer) (Object) this);
	}
}
