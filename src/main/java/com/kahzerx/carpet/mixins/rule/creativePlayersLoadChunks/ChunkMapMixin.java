package com.kahzerx.carpet.mixins.rule.creativePlayersLoadChunks;

import com.kahzerx.carpet.CarpetSettings;
import net.minecraft.server.ChunkMap;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC>=11300
import java.util.function.Predicate;
//#else
//$$ import com.google.common.base.Predicates;
//$$ import com.google.common.base.Predicate;
//#endif

//#if MC>=10900
@Mixin(ChunkMap.class)
public class ChunkMapMixin {

	@Shadow @Final @Mutable
	private static Predicate<ServerPlayerEntity> GENERATES_CHUNKS;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(CallbackInfo ci) {
		Predicate<ServerPlayerEntity> LOAD_CHUNKS_IN_CREATIVE = player ->
			(!player.abilities.creativeMode || CarpetSettings.creativePlayersLoadChunks);

		//#if MC>11300
		GENERATES_CHUNKS = GENERATES_CHUNKS.and(LOAD_CHUNKS_IN_CREATIVE);
		//#else
		//$$ GENERATES_CHUNKS = Predicates.and(GENERATES_CHUNKS, LOAD_CHUNKS_IN_CREATIVE);
		//#endif

	}
}
//#else
//$$ @Mixin(ChunkMap.class)
//$$ public class ChunkMapMixin {}
//#endif
