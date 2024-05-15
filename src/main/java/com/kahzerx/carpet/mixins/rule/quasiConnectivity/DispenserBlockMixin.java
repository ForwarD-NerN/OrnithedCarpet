package com.kahzerx.carpet.mixins.rule.quasiConnectivity;

import com.kahzerx.carpet.helpers.QuasiConnectivity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
	@Redirect(method = "neighborChanged", at = @At(value = "INVOKE",
		//#if MC>10710
		target = "Lnet/minecraft/world/World;hasNeighborSignal(Lnet/minecraft/util/math/BlockPos;)Z",
		//#else
		//$$ target = "Lnet/minecraft/world/World;hasNeighborSignal(III)Z",
		//#endif
		ordinal = 1))
	private boolean hasSignal(World world,
							  //#if MC>10710
							  BlockPos pos
							  //#else
							  //$$ int x, int y, int z
							  //#endif
	) {
		return QuasiConnectivity.hasQuasiSignal(world,
			//#if MC>10710
			pos
			//#else
			//$$ new BlockPos(x, y, z)
			//#endif
		);
	}
}
