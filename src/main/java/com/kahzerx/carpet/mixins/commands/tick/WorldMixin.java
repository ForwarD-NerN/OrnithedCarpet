package com.kahzerx.carpet.mixins.commands.tick;

import com.kahzerx.carpet.fakes.WorldTickRate;
import net.minecraft.entity.Entity;
//#if MC>10710
import net.minecraft.util.Tickable;
//#else
//$$ import net.minecraft.block.entity.BlockEntity;
//#endif
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public abstract class WorldMixin implements WorldTickRate {
	@Redirect(method = "tickEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V"))
	private void onEntityTick(Entity entity) {
		if (tickRateManager().shouldEntityTick(entity)) {
			entity.tick();
		}
	}

	@Redirect(method = "tickEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;updateEntity(Lnet/minecraft/entity/Entity;)V"))
	private void onUpdate(World world, Entity entity) {
		if (tickRateManager().shouldEntityTick(entity)) {
			world.updateEntity(entity);
		}
	}

	@Redirect(method = "tickEntities", at = @At(value = "INVOKE", target =
		//#if MC>10710
		"Lnet/minecraft/util/Tickable;tick()V"
		//#else
		//$$ "Lnet/minecraft/block/entity/BlockEntity;tick()V"
		//#endif
	))
	private void onBlockEntityTick(
		//#if MC>10710
		Tickable instance
		//#else
		//$$ BlockEntity instance
		//#endif
	) {
		if (tickRateManager().runsNormally()) {
			instance.tick();
		}
	}
}
