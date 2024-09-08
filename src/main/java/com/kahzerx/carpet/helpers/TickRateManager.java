package com.kahzerx.carpet.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;

public class TickRateManager {
	public static final int PLAYER_GRACE = 2;
	protected float tickRate = 20.0f;
	protected float mspt = 50.0f;
	protected int playerActivityTimeout = 0;
	protected boolean runGameElements = true;
	// deep freeze is only used serverside
	protected boolean deepFreeze = false;
	protected boolean isGamePaused = false;

	public void setTickRate(float rate) {
		tickRate = rate;
		long msptt = (long) (1000.0 / tickRate);
		if (msptt <= 0L) {
			msptt = 1L;
			tickRate = 1000.0f;
		}
		mspt = msptt;
	}

	public float tickRate() {
		return tickRate;
	}

	public boolean gameIsPaused() {
		return isGamePaused;
	}

	public float mspt() {
		return mspt;
	}

	public boolean runsNormally() {
		return runGameElements;
	}

	public void setPlayerActiveTimeout(int timeout) {
		playerActivityTimeout = timeout;
	}

	public int getPlayerActiveTimeout() {
		return playerActivityTimeout;
	}

	public void setFrozenState(boolean isPaused, boolean isDeepFreeze) {
		isGamePaused = isPaused;
		deepFreeze = isPaused && isDeepFreeze;
	}

	public void tick() {
		if (playerActivityTimeout > 0) {
			playerActivityTimeout--;
		}
		if (isGamePaused) {
			runGameElements = playerActivityTimeout >= PLAYER_GRACE;
		} else {
			runGameElements = true;
		}
	}

	public boolean shouldEntityTick(Entity e) {
		return (runsNormally() || (e instanceof PlayerEntity) ||
			//#if MC>10809
			e.getControllingPassenger() instanceof PlayerEntity
			//#else
			//$$ e.rider instanceof PlayerEntity
			//#endif
		);
	}
}
