package com.kahzerx.carpet.mixins.commands.tick;

import com.kahzerx.carpet.fakes.MinecraftServerTickRate;
import com.kahzerx.carpet.helpers.ServerTickRateManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerTickRate {
	//#if MC>10710
	@Shadow
	private long nextTickTime;
	//#endif
	@Shadow
	private long lastWarnTime;

	//#if MC>11202
	@Shadow
	protected abstract boolean hasTimeLeft();
	//#endif

	//#if MC<=11202
	//$$ @Shadow
	//$$ public abstract void tick();
	//#endif

	@Unique
	private ServerTickRateManager serverTickRateManager;

	@Override
	public ServerTickRateManager getTickRateManager() {
		return serverTickRateManager;
	}

	private float carpetMsptAccum = 0.0f;
	private long msThisTick = 0L;

	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInit(CallbackInfo ci) {
		if (this.getTickRateManager() == null) {
			this.serverTickRateManager = new ServerTickRateManager((MinecraftServer) (Object) this);
		}
	}

	@Redirect(method = "run", at = @At(value = "INVOKE", target =
		//#if MC>11202
		"Lnet/minecraft/util/Utils;getTimeMillis()J",
		//#else
		//$$ "Lnet/minecraft/server/MinecraftServer;getTimeMillis()J",
		//#endif
		ordinal = 1
	))
	private long onGetTimeMillis() {
		if (this.getTickRateManager() == null) {
			this.serverTickRateManager = new ServerTickRateManager((MinecraftServer) (Object) this);
		}
		float mspt = serverTickRateManager.mspt();
		if (serverTickRateManager.isInWarpSpeed() && serverTickRateManager.continueWarp()) {
			msThisTick = 0L;
			carpetMsptAccum = mspt;
			//#if MC>11202
			this.nextTickTime = this.lastWarnTime = Utils.getTimeMillis();
			//#elseif MC>10710
			//$$ this.nextTickTime = this.lastWarnTime = System.currentTimeMillis();
			//#endif
			//#if MC<=11202
			//$$ tick();
			//#endif
		} else {
			if (Math.abs(carpetMsptAccum - mspt) > 1.0f) {
				carpetMsptAccum = mspt;
			}
			msThisTick = (long) carpetMsptAccum;
			carpetMsptAccum += mspt - msThisTick;
		}
		//#if MC>11202
		return Utils.getTimeMillis();
		//#else
		//$$ return System.currentTimeMillis();
		//#endif
	}

	@ModifyConstant(method = "run", constant = @Constant(longValue = 2000L))
	private long slow1(long constant) {
		return (long) (1000L+20*serverTickRateManager.mspt());
	}

	@ModifyConstant(method = "run", constant = @Constant(longValue = 15000L))
	private long slow2(long constant) {
		return (long) (10000L+100*serverTickRateManager.mspt());
	}

	@ModifyConstant(method = "run", constant = @Constant(longValue = 50L))
	private long customMspt(long constant) {
		return this.serverTickRateManager.isInWarpSpeed() ? 1L : this.msThisTick;
	}

	//#if MC>11202
	@Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tick(Ljava/util/function/BooleanSupplier;)V"))
	private void onHasTimeLeft(MinecraftServer instance, BooleanSupplier booleanSupplier) {
		instance.tick(serverTickRateManager.isInWarpSpeed() ? () -> true : booleanSupplier);
	}
	//#endif

	@Inject(method = "run", at = @At(value = "INVOKE", target =
		//#if MC>11202
		"Lnet/minecraft/server/MinecraftServer;hasTimeLeft()Z"
		//#else
		//$$ "Lnet/minecraft/server/world/ServerWorld;canSkipNight()Z"
		//#endif
		, shift = At.Shift.BEFORE))
	private void onAdjustTime(CallbackInfo ci) {
		//#if MC>10710
		if (this.serverTickRateManager.isInWarpSpeed()) {
			this.nextTickTime -= (long) serverTickRateManager.mspt() + msThisTick;
		}
		//#endif
	}

	//#if MC<=10710
	//$$	@ModifyVariable(method = "run", at = @At("STORE"), ordinal = 0)
	//$$	private long modified(long var1) {
	//$$ 		return this.serverTickRateManager.isInWarpSpeed() ? var1 - (long) serverTickRateManager.mspt() + msThisTick : var1;
	//$$	}
	//#endif

	//#if MC>11202
	@Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;hasTimeLeft()Z"))
	private boolean onWait(MinecraftServer instance) {
		if (serverTickRateManager.isInWarpSpeed()) {
			return false;
		} else {
			return hasTimeLeft();
		}
	}
	//#endif

	//#if MC<=11202
	//$$	@Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(JJ)J"))
	//$$	private long max(long a, long b) {
	//$$		return this.serverTickRateManager.isInWarpSpeed() ? 1L : Math.max(a, b);
	//$$	}
	//#endif
}
