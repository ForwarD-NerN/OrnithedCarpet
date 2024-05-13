package com.kahzerx.carpet.mixins.commands.tick;

import com.kahzerx.carpet.fakes.WorldTickRate;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public abstract class WorldMixin implements WorldTickRate {
	@Redirect(method = "tickEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V"))
	private void onEntityTick(Entity entity) {
		if (tickRateManager().shouldEntityTick(entity)) {
//			System.out.println(entity);
//			System.out.println("tick!");
			entity.tick();
		}
	}

	@Redirect(method = "tickEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;updateEntity(Lnet/minecraft/entity/Entity;)V"))
	private void onUpdate(World world, Entity entity) {
		if (tickRateManager().shouldEntityTick(entity)) {
//			System.out.println(entity);
//			System.out.println("tick!");
			world.updateEntity(entity);
		}
	}
}
