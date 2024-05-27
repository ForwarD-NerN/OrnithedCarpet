package com.kahzerx.carpet.network;

import com.kahzerx.carpet.CarpetExtension;
import com.kahzerx.carpet.CarpetServer;
import com.kahzerx.carpet.CarpetSettings;
import com.kahzerx.carpet.api.settings.CarpetRule;
import com.kahzerx.carpet.api.settings.InvalidRuleValueException;
import com.kahzerx.carpet.api.settings.SettingsManager;
import com.kahzerx.carpet.fakes.WorldTickRate;
import com.kahzerx.carpet.helpers.TickRateManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;

import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

public class ClientNetworkHandler {
    private static final Map<String, BiConsumer<LocalClientPlayerEntity, NbtElement>> dataHandlers = new HashMap<>();
	public static Lock lockedClientPlayer = new ReentrantLock();
	public static Condition clientPlayerLoaded = lockedClientPlayer.newCondition();

    static {
        dataHandlers.put(CarpetClient.HI, (p, t) -> onHi((NbtString) t));
        dataHandlers.put("Rules", (p, t) -> {
            NbtCompound ruleset = (NbtCompound) t;
            for (Object k : ruleset.getKeys()) {
				String ruleKey = (String) k;
				NbtCompound ruleNBT = (NbtCompound) ruleset.get(ruleKey);
                SettingsManager manager = null;
                String ruleName;
                if (ruleNBT.contains("Manager")) {
                    ruleName = ruleNBT.getString("Rule");
                    String managerName = ruleNBT.getString("Manager");
                    if (managerName.equals("carpet")) {
                        manager = CarpetServer.settingsManager;
                    } else {
                        for (CarpetExtension extension : CarpetServer.extensions) {
                            SettingsManager eManager = extension.extensionSettingsManager();
                            if (eManager != null && managerName.equals(eManager.identifier())) {
                                manager = eManager;
                                break;
                            }
                        }
                    }
                } else {
                    manager = CarpetServer.settingsManager;
                    ruleName = ruleKey;
                }
                CarpetRule<?> rule = (manager != null) ? manager.getCarpetRule(ruleName) : null;
                if (rule != null) {
                    String value = ruleNBT.getString("Value");
                    try {
                        rule.set(null, value);
                    } catch (InvalidRuleValueException ignored) { }
                }
            }
        });
		dataHandlers.put("TickRate", (p, t) -> {
			TickRateManager tickRateManager = ((WorldTickRate) p.world).tickRateManager();
			tickRateManager.setTickRate(((NbtFloat) t).getFloat());
		});
		dataHandlers.put("TickingState", (p, t) -> {
			NbtCompound tickingState = (NbtCompound) t;
			TickRateManager tickRateManager = ((WorldTickRate) p.world).tickRateManager();
			tickRateManager.setFrozenState(tickingState.getBoolean("is_paused"), tickingState.getBoolean("deepFreeze"));
		});
		dataHandlers.put("TickPlayerActiveTimeout", (p, t) -> {
			TickRateManager tickRateManager = ((WorldTickRate) p.world).tickRateManager();
			tickRateManager.setPlayerActiveTimeout(((NbtInt) t).getInt());
		});
        dataHandlers.put("clientCommand", (p, t) -> CarpetClient.onClientCommand(t));
    }

    // Ran on the Main Minecraft Thread

    private static void onHi(NbtString versionElement) {
        CarpetClient.setCarpetServer();
        CarpetClient.serverCarpetVersion = versionElement.asString();
        if (CarpetSettings.carpetVersion.equals(CarpetClient.serverCarpetVersion)) {
            CarpetSettings.LOG.info("Joined carpet server with matching carpet version");
        } else {
            CarpetSettings.LOG.warn("Joined carpet server with another carpet version: " + CarpetClient.serverCarpetVersion);
        }
        // We can ensure that this packet is
        // processed AFTER the player has joined
        respondHello();
    }

    public static void respondHello() {
        NbtCompound data = new NbtCompound();
        data.putString(CarpetClient.HELLO, CarpetSettings.carpetVersion);
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeNbtCompound(data);
		// TODO wait for clientPlayer != null
		if (CarpetClient.getClientPlayer() == null) {
			lockedClientPlayer.lock();
		}
		try {
			while (CarpetClient.getClientPlayer() == null) {
				clientPlayerLoaded.await();
			}
		} catch (InterruptedException ignored) {
		} finally {
			lockedClientPlayer.lock();  // TODO unsure...
            lockedClientPlayer.unlock();
        }
        CarpetClient.getClientPlayer().networkHandler.sendPacket(new CustomPayloadC2SPacket(
			//#if MC>11202
			CarpetClient.CARPET_CHANNEL,
			//#else
			//$$ CarpetClient.CARPET_CHANNEL.toString(),
			//#endif
			buf
		));
    }

    public static void onServerData(NbtCompound compound, LocalClientPlayerEntity player) {
        for (Object k : compound.getKeys()) {
			String key = (String) k;
            if (dataHandlers.containsKey(key)) {
                try {
                    dataHandlers.get(key).accept(player, compound.get(key));
                } catch (Exception exc) {
                    CarpetSettings.LOG.info("Corrupt carpet data for " + key);
                }
            } else {
                CarpetSettings.LOG.error("Unknown carpet data: " + key);
            }
        }
    }

    public static void clientCommand(String command) {
		NbtCompound tag = new NbtCompound();
        tag.putString("id", command);
        tag.putString("command", command);
		NbtCompound outer = new NbtCompound();
        outer.put("clientCommand", tag);
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeNbtCompound(outer);
		CarpetClient.getClientPlayer().networkHandler.sendPacket(new CustomPayloadC2SPacket(
			//#if MC>11202
			CarpetClient.CARPET_CHANNEL,
			//#else
			//$$ CarpetClient.CARPET_CHANNEL.toString(),
			//#endif
			buf
		));
    }
}
