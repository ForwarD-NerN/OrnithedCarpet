package com.kahzerx.carpet.mixins.server;

import com.google.gson.JsonElement;
import com.kahzerx.carpet.CarpetServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.WorldGeneratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Inject(method = "tick", at = @At(value = "INVOKE", target =
		//#if MC>11202
		"Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V"
		//#else
		//$$ "Lnet/minecraft/server/MinecraftServer;tickWorlds()V"
		//#endif
		, shift = At.Shift.BEFORE, ordinal = 0))
	//#if MC>11202
	private void onTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
	//#else
	//$$ private void onTick(CallbackInfo ci) {
	//#endif
		CarpetServer.onTick((MinecraftServer) (Object) this);
	}

	@Inject(method = "stop", at = @At(value = "HEAD"))
	private void onServerClosed(CallbackInfo ci) {
		CarpetServer.onServerClosed((MinecraftServer) (Object) this);
	}

	@Inject(method = "stop", at = @At(value = "TAIL"))
	private void onServerDoneClosing(CallbackInfo ci) {
		CarpetServer.onServerDoneClosing((MinecraftServer) (Object) this);
	}

	@Inject(method = "loadWorld", at = @At(value = "HEAD"))
	//#if MC>11202
	private void onLoadWorld(String string, String string2, long l, WorldGeneratorType worldGeneratorType, JsonElement jsonElement, CallbackInfo ci) {
	//#else
	//$$private void onLoadWorld(String string, String string2, long l, WorldGeneratorType worldGeneratorType, String string3, CallbackInfo ci) {
	//#endif
		CarpetServer.onServerLoaded((MinecraftServer) (Object) this);
	}

	@Inject(method = "loadWorld", at = @At("RETURN"))
	//#if MC>11202
	private void onLoadedWorlds(String string, String string2, long l, WorldGeneratorType worldGeneratorType, JsonElement jsonElement, CallbackInfo ci) {
	//#else
	//$$private void onLoadedWorlds(String string, String string2, long l, WorldGeneratorType worldGeneratorType, String string3, CallbackInfo ci) {
	//#endif
		CarpetServer.onServerLoadedWorlds((MinecraftServer) (Object) this);
	}
}
