package com.kahzerx.carpet.helpers;

import com.kahzerx.carpet.CarpetSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class QuasiConnectivity {
	public static boolean hasQuasiSignal(World world, BlockPos pos) {
		for (int i = 1; i <= CarpetSettings.quasiConnectivity; i++) {
			//#if MC>10710
			BlockPos up = new BlockPos(pos.getX(), pos.getY() + i, pos.getZ());
			//#else
			//$$ BlockPos up = new BlockPos(pos.x, pos.y + i, pos.z);
			//#endif

			//#if MC>10710
			if (up.getY() < 0 || up.getY() >= 256) {
			//#else
			//$$ if (up.y < 0 || up.y >= 256) {
			//#endif
				break;
			}
			//#if MC>10710
			if (world.hasNeighborSignal(up)) {
			//#else
			//$$ if (world.hasNeighborSignal(up.x, up.y, up.z)) {
			//#endif
				return true;
			}
		}

		return false;
	}
}
