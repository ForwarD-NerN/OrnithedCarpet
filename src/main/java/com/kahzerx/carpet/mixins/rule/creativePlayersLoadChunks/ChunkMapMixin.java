package com.kahzerx.carpet.mixins.rule.creativePlayersLoadChunks;

import com.kahzerx.carpet.CarpetSettings;
import com.kahzerx.carpet.fakes.ChunkHolderAccess;
import com.kahzerx.carpet.fakes.ChunkMapAccess;
import net.minecraft.server.ChunkMap;

import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

//#if MC>=10900
import net.minecraft.server.ChunkHolder;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin implements ChunkMapAccess {

	//#if MC>=10900
	@Shadow
	protected abstract ChunkHolder getOrAddChunk(int par1, int par2);

	@Shadow
	public abstract @Nullable ChunkHolder getChunk(int chunkX, int chunkZ);
	//#else
	//$$ @Shadow protected abstract ChunkHolder getChunk(int chunkX, int chunkZ, boolean add);
	//#endif

	@Shadow
	protected abstract boolean isChunkWithinView(int chunkX, int chunkZ, int playerChunkX, int playerChunkZ, int chunkViewDistance);

	@Shadow
	@Final
	private ServerWorld world;

	@Shadow
	private int chunkViewDistance;

	@Redirect(method = "movePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ChunkMap;isChunkWithinView(IIIII)Z", ordinal = 0))
	private boolean movePlayer(ChunkMap instance, int chunkX, int chunkZ, int playerChunkX, int playerChunkZ, int chunkViewDistance, ServerPlayerEntity player) {
		boolean withinView = isChunkWithinView(chunkX, chunkZ, playerChunkX, playerChunkZ, chunkViewDistance);

		if(!withinView && shouldNotLoadNewChunks(player)) {
			ChunkSource source = world.getChunkSource();

			WorldChunk chunk =
			//#if MC>=11300
				source.getChunk(chunkX, chunkZ, false, false);
			//#else
			//$$source.getLoadedChunk(chunkX, chunkZ);
			//#endif

			if(chunk != null) {
				//#if MC>=10900
				this.getOrAddChunk(chunkX, chunkZ).addPlayer(player);
				//#else
				//$$ this.getChunk(chunkX, chunkZ, false).addPlayer(player);
				//#endif
			}

			return true;
		}
		return withinView;
	}

	private boolean shouldNotLoadNewChunks(ServerPlayerEntity player) {
		if(!CarpetSettings.creativePlayersLoadChunks) {
			return player.abilities.creativeMode;
		}
		return false;
	}

	@Override
	public void syncChunks(ServerPlayerEntity player) {
		int i = (int)player.x >> 4;
		int j = (int)player.z >> 4;

		for (int chunkX = i - this.chunkViewDistance; chunkX <= i + this.chunkViewDistance; chunkX++) {
			for (int chunkZ = j - this.chunkViewDistance; chunkZ <= j + this.chunkViewDistance; chunkZ++) {
				//#if MC>=10900
				this.getOrAddChunk(chunkX, chunkZ).addPlayer(player);
				//#else
				//$$ this.getChunk(chunkX, chunkZ, true).addPlayer(player);
				//#endif
			}
		}
	}

	@Override
	public void unloadNearestChunks(ServerPlayerEntity player) {
		int i = (int)player.x >> 4;
		int j = (int)player.z >> 4;

		for (int chunkX = i - this.chunkViewDistance; chunkX <= i + this.chunkViewDistance; chunkX++) {
			for (int chunkZ = j - this.chunkViewDistance; chunkZ <= j + this.chunkViewDistance; chunkZ++) {

				boolean canUnload = world.dimension.canChunkUnload(chunkX, chunkZ);

				ChunkHolder holder =
				//#if MC>=10900
					this.getChunk(chunkX, chunkZ);
				//#else
				//$$ this.getChunk(chunkX, chunkZ, false);
				//#endif

				if(canUnload && holder != null)
					((ChunkHolderAccess)holder).removePlayerQuietly(player);

			}
		}
	}
}
//#else
//$$ @Mixin(ChunkMap.class)
//$$ public abstract class ChunkMapMixin {}
//#endif
