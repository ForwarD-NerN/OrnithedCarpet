package com.kahzerx.carpet.mixins.rule.quasiConnectivity;

import com.kahzerx.carpet.helpers.QuasiConnectivity;
import net.minecraft.block.PistonBaseBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {
	@Inject(method = "shouldExtend", at = @At(value = "INVOKE",
		//#if MC>10710
		target = "Lnet/minecraft/util/math/BlockPos;up()Lnet/minecraft/util/math/BlockPos;"
		//#else
		//$$ target = "Lnet/minecraft/world/World;hasSignal(IIII)Z", ordinal = 7
		//#endif
	), cancellable = true)
	private void hasSignal(World world,
						   //#if MC>10710
						   BlockPos pos, Direction facing,
						   //#else
						   //$$ int x, int y, int z, int facing,
						   //#endif
						   CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(QuasiConnectivity.hasQuasiSignal(world,
			//#if MC>10710
			pos
			//#else
			//$$ new BlockPos(x, y, z)
			//#endif
		));
	}
}
