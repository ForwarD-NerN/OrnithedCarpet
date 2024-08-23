package com.kahzerx.carpet.helpers;

import com.kahzerx.carpet.CarpetServer;
import com.kahzerx.carpet.network.ServerNetworkHandler;
import com.kahzerx.carpet.utils.Messenger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
//#if MC>=11300
import net.minecraft.server.command.handler.CommandManager;
//#else
//$$ import net.minecraft.server.command.handler.CommandHandler;
//#endif
//#if MC>=11300
import net.minecraft.server.command.source.CommandSourceStack;
//#else
//$$ import net.minecraft.server.command.source.CommandSource;
//#endif
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ServerTickRateManager extends TickRateManager {
	private long remainingWarpTicks = 0;
	private long tickWarpStartTime = 0;
	private long scheduledCurrentWarpTicks = 0;
	private ServerPlayerEntity warpResponsiblePlayer = null;
	private String tickWarpCallback = null;
	//#if MC>=11300
	private CommandSourceStack warpResponsibleSource = null;
	//#else
	//$$ private CommandSource warpResponsibleSource = null;
	//#endif


	private MinecraftServer server;

	private final Map<String, BiConsumer<String, Float>> tickrateListeners = new HashMap<>();
	private static final float MIN_TICKRATE = 0.01f;

	public ServerTickRateManager(MinecraftServer server) {
		this.server = server;
	}

	public boolean isInWarpSpeed() {
		return tickWarpStartTime != 0;
	}


	@Override
	public boolean shouldEntityTick(Entity e) {
		return (runsNormally() || (e instanceof PlayerEntity));
	}

	public boolean deeplyFrozen() {
		return deepFreeze;
	}

	@Override
	public void setFrozenState(boolean isPaused, boolean isDeepFreeze) {
		super.setFrozenState(isPaused, isDeepFreeze);
		ServerNetworkHandler.updateFrozenStateToConnectedPlayers();
	}

	public void resetPlayerActivity() {
		if (playerActivityTimeout < PLAYER_GRACE) {
			playerActivityTimeout = PLAYER_GRACE;
			ServerNetworkHandler.updateTickPlayerActiveTimeoutToConnectedPlayers();
		}
	}

	public void stepGameIfPaused(int ticks) {
		playerActivityTimeout = PLAYER_GRACE + ticks;
		ServerNetworkHandler.updateTickPlayerActiveTimeoutToConnectedPlayers();
	}

	public Text requestGameToWarpSpeed(ServerPlayerEntity player, int advance, String callback,
									   //#if MC>=11300
									   CommandSourceStack source
									   //#else
									   //$$ CommandSource source
									   //#endif
	) {
		if (0 == advance) {
			tickWarpCallback = null;
			if (source != warpResponsibleSource) {
				warpResponsibleSource = null;
			}
			if (remainingWarpTicks > 0) {
				finishTickWarp();
				warpResponsibleSource = null;
				return Messenger.c("gi Warp interrupted");
			}
			return Messenger.c("ri No warp in progress");
		}
		if (remainingWarpTicks > 0) {
			String who = "Another player";
			if (warpResponsiblePlayer != null) {
				//#if MC>10809
				who = warpResponsiblePlayer.getScoreboardName();
				//#else
				//$$ who = warpResponsiblePlayer.getName();
				//#endif
			}
			return Messenger.c("l " + who + " is already advancing time at the moment. Try later or ask them");
		}
		warpResponsiblePlayer = player;
		tickWarpStartTime = System.nanoTime();
		scheduledCurrentWarpTicks = advance;
		remainingWarpTicks = advance;
		tickWarpCallback = callback;
		warpResponsibleSource = source;
		return Messenger.c("gi Warp speed ....");
	}

	// should be private
	public void finishTickWarp() {
		long completedTicks = scheduledCurrentWarpTicks - remainingWarpTicks;
		double msToComplete = System.nanoTime() - tickWarpStartTime;
		if (msToComplete == 0.0) {
			msToComplete = 1.0;
		}
		msToComplete /= 1000000.0;
		int tps = (int) (1000.0D * completedTicks / msToComplete);
		double mspt = msToComplete / completedTicks;
		scheduledCurrentWarpTicks = 0;
		tickWarpStartTime = 0;
		if (tickWarpCallback != null) {
			//#if MC>=11300
			CommandManager icommandmanager = warpResponsibleSource.getServer().getCommandHandler();
			//#elseif MC>10809
			//$$ CommandHandler icommandmanager = warpResponsibleSource.getServer().getCommandHandler();
			//#else
			//$$ CommandHandler icommandmanager = MinecraftServer.getInstance().getCommandHandler();
			//#endif
			try {
				icommandmanager.run(warpResponsibleSource, tickWarpCallback);
			} catch (Throwable var23) {
				if (warpResponsiblePlayer != null) {
					Messenger.m(warpResponsiblePlayer, "r Command Callback failed - unknown error: ", "rb /" + tickWarpCallback, "/" + tickWarpCallback);
				}
			}
			tickWarpCallback = null;
			warpResponsibleSource = null;
		}
		if (warpResponsiblePlayer != null) {
			Messenger.m(warpResponsiblePlayer, String.format("gi ... Time warp completed with %d tps, or %.2f mspt", tps, mspt));
			warpResponsiblePlayer = null;
		} else {
			Messenger.printServerMessage(CarpetServer.minecraftServer, String.format("... Time warp completed with %d tps, or %.2f mspt", tps, mspt));
		}
		remainingWarpTicks = 0;

	}

	public boolean continueWarp() {
		if (!runGameElements) {
			return false;
		}
		if (remainingWarpTicks > 0) {
			if (remainingWarpTicks == scheduledCurrentWarpTicks) {
				tickWarpStartTime = System.nanoTime();
			}
			remainingWarpTicks -= 1;
			return true;
		} else {
			finishTickWarp();
			return false;
		}
	}

	@Override
	public void setTickRate(float rate) {
		setTickRate(rate, true);
	}

	public void setTickRate(float rate, boolean update) {
		super.setTickRate(rate);
		if (update) {
			notifyTickrateListeners("carpet");
		}
	}

	private void tickrateChanged(String modId, float rate) {
		if (rate < MIN_TICKRATE) {
			rate = MIN_TICKRATE;
		}

		tickRate = rate;
		mspt = 1000.0f / tickRate;

		notifyTickrateListeners(modId);
	}

	private void notifyTickrateListeners(String originModId) {
		synchronized (tickrateListeners) {
			for (Map.Entry<String, BiConsumer<String, Float>> listenerEntry : tickrateListeners.entrySet()) {
				if (originModId == null || !originModId.equals(listenerEntry.getKey())) {
					listenerEntry.getValue().accept(originModId, tickRate);
				}
			}
		}
		ServerNetworkHandler.updateTickSpeedToConnectedPlayers();
	}

	public BiConsumer<String, Float> addTickrateListener(String modId, BiConsumer<String, Float> tickrateListener) {
		synchronized (tickrateListeners) {
			tickrateListeners.put(modId, tickrateListener);
		}
		return this::tickrateChanged;
	}
}
