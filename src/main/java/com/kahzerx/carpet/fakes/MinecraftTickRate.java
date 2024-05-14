package com.kahzerx.carpet.fakes;

import com.kahzerx.carpet.helpers.ServerTickRateManager;
import com.kahzerx.carpet.helpers.TickRateManager;

import java.util.Optional;

public interface MinecraftTickRate {
	Optional<TickRateManager> getTickRateManager();
}
