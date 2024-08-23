package com.kahzerx.carpet.commands;

import com.kahzerx.carpet.CarpetSettings;
import com.kahzerx.carpet.fakes.MinecraftServerTickRate;
import com.kahzerx.carpet.helpers.ServerTickRateManager;
import com.kahzerx.carpet.utils.CommandHelper;
import com.kahzerx.carpet.utils.Messenger;
//#if MC>=11300
import com.mojang.brigadier.CommandDispatcher;
//#else
//$$ import net.minecraft.server.MinecraftServer;
//$$ import java.util.Collections;
//$$ import java.util.List;
//$$ import net.minecraft.util.math.BlockPos;
//$$ import org.jetbrains.annotations.Nullable;
//$$ import net.minecraft.server.command.exception.CommandSyntaxException;
//$$ import net.minecraft.server.command.AbstractCommand;
//$$ import net.minecraft.server.command.exception.CommandException;
//$$ import net.minecraft.server.command.source.CommandSource;
//$$ import com.google.common.collect.Iterables;
//$$ import java.util.Arrays;
//$$ import java.util.ArrayList;
//#endif
//#if MC<=10710
//$$ import net.minecraft.server.command.Command;
//$$ import org.jetbrains.annotations.NotNull;
//#endif
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.server.command.handler.CommandManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
//#if MC>=11300
import net.minecraft.server.command.source.CommandSourceStack;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
//#else
//$$ import net.minecraft.server.command.source.CommandSource;
//#endif
import java.util.Collections;

public class TickCommand
//#if MC<=11202
//$$ extends AbstractCommand
//#endif
{
	//#if MC>=11300
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(CommandManager.literal("tick").
			requires((p) -> CommandHelper.canUseCommand(p, CarpetSettings.commandTick)).
			then(CommandManager.literal("rate").
				executes((c) -> queryTps(c.getSource())).
				then(CommandManager.argument("rate", FloatArgumentType.floatArg(0.1F, 500.0F)).
					suggests((c, b) -> suggest(Collections.singletonList("20.0"), b)).
					executes((c) -> setTps(c.getSource(), FloatArgumentType.getFloat(c, "rate"))))).
			then(CommandManager.literal("warp").
				executes((c) -> setWarp(c.getSource(), 0, null)).
				then(CommandManager.argument("ticks", IntegerArgumentType.integer(0)).
					suggests((c, b) -> suggest(Arrays.asList("3600", "72000"), b)).
					executes((c) -> setWarp(c.getSource(), IntegerArgumentType.getInteger(c, "ticks"), null)).
					then(CommandManager.argument("tail command", StringArgumentType.greedyString()).
						executes((c) -> setWarp(c.getSource(), IntegerArgumentType.getInteger(c, "ticks"), StringArgumentType.getString(c, "tail command")))))).
			then(CommandManager.literal("freeze").
				executes((c) -> toggleFreeze(c.getSource(), false)).
				then(CommandManager.literal("status").
					executes((c) -> freezeStatus(c.getSource()))).
				then(CommandManager.literal("deep").
					executes((c) -> toggleFreeze(c.getSource(), true))).
				then(CommandManager.literal("on").
					executes((c) -> setFreeze(c.getSource(), false, true)).
					then(CommandManager.literal("deep").
						executes((c) -> setFreeze(c.getSource(), true, true)))).
				then(CommandManager.literal("off").
					executes((c) -> setFreeze(c.getSource(), false, false)))));
	}
	//#endif
	//#if MC<=11202
	//$$	@Override
	//$$	public String getName() {
	//$$		return "tick";
	//$$	}
	//$$
	//$$	@Override
	//$$	public String getUsage(CommandSource commandSource) {
	//$$		return this.getName() + " <option>";
	//$$	}
	//$$	@Override
		//#if MC>10809
		//$$ public void run(MinecraftServer minecraftServer, CommandSource commandSource, String[] strings) throws CommandException {
		//#else
		//$$ public void run(CommandSource commandSource, String[] strings) throws CommandException {
		//#endif
	//$$ 	     if (strings.length == 1) {
	//$$ 		    if ("freeze".equalsIgnoreCase(strings[0])) {
	//$$ 			    toggleFreeze(commandSource, false);
	//$$ 	    	} else if ("rate".equalsIgnoreCase(strings[0])) {
	//$$ 				queryTps(commandSource);
	//$$ 	    	} else if ("warp".equalsIgnoreCase(strings[0])) {
	//$$ 				setWarp(commandSource, 0, null);
	//$$			}
	//$$    	 }
	//$$ 		if (strings.length == 2) {
	//$$ 			if ("freeze".equalsIgnoreCase(strings[0])) {
	//$$      		    if ("status".equalsIgnoreCase(strings[1])) {
	//$$ 					freezeStatus(commandSource);
	//$$                } else if("deep".equalsIgnoreCase(strings[1])) {
	//$$ 					toggleFreeze(commandSource, true);
	//$$                } else if("on".equalsIgnoreCase(strings[1])) {
	//$$ 					setFreeze(commandSource, false, true);
	//$$                } else if("off".equalsIgnoreCase(strings[1])) {
	//$$ 					setFreeze(commandSource, false, false);
	//$$ 				}
	//$$ 	  		} else if ("rate".equalsIgnoreCase(strings[0])) {
	//$$ 				String sRate = strings[1];
	//$$ 				float rate;
	//$$ 				try {
	//$$ 					rate = Float.parseFloat(sRate);
	//$$ 				} catch (Exception e) {
	//$$ 					Messenger.m(commandSource, "A numeric value is required");
	//$$ 					return;
	//$$				}
	//$$ 				if (rate < 0.1F || rate > 500.0F) {
	//$$ 					Messenger.m(commandSource, "Rate must be between 0.1 and 500");
	//$$ 					return;
	//$$				}
	//$$ 				setTps(commandSource, rate);
	//$$			} else if ("warp".equalsIgnoreCase(strings[0])) {
	//$$                String sWarp = strings[1];
	//$$                int warp;
	//$$ 				try {
	//$$ 					warp = Integer.parseInt(sWarp);
	//$$ 				} catch (Exception e) {
	//$$ 					Messenger.m(commandSource, "An integer value is required");
	//$$ 					return;
	//$$				}
	//$$ 				if (warp < 0) {
	//$$ 					Messenger.m(commandSource, "Rate must be positive");
	//$$ 					return;
	//$$				}
	//$$                setWarp(commandSource, warp, null);
	//$$            }
	//$$		}
	//$$ 		if (strings.length == 3) {
	//$$ 			if ("freeze".equalsIgnoreCase(strings[0])) {
	//$$ 				if ("on".equalsIgnoreCase(strings[1])) {
	//$$ 					if ("deep".equalsIgnoreCase(strings[2])) {
	//$$ 						setFreeze(commandSource, true, true);
	//$$ 					}
	//$$ 				}
	//$$			} else if ("warp".equalsIgnoreCase(strings[0])) {
	//$$                String sWarp = strings[1];
	//$$                int warp;
	//$$ 				try {
	//$$ 					warp = Integer.parseInt(sWarp);
	//$$ 				} catch (Exception e) {
	//$$ 					Messenger.m(commandSource, "An integer value is required");
	//$$ 					return;
	//$$				}
	//$$ 				if (warp < 0) {
	//$$ 					Messenger.m(commandSource, "Rate must be positive");
	//$$ 					return;
	//$$				}
	//$$                String[] tailCommand = Arrays.copyOfRange(strings, 2, strings.length);
	//$$                String command = String.join(" ", tailCommand);
	//$$                setWarp(commandSource, warp, command);
	//$$            }
	//$$ 		}
	//$$     }
	//$$
	//$$	@Override
	//#if MC>10809
	//$$   public boolean canUse(MinecraftServer minecraftServer, CommandSource commandSource) {
	//#else
	//$$   public boolean canUse(CommandSource commandSource) {
	//#endif
	//$$   return CommandHelper.canUseCommand(commandSource, CarpetSettings.commandTick);
	//$$ }
	//$$
	//$$	@Override
	//#if MC>10809
	//$$	public List<String> getSuggestions(MinecraftServer minecraftServer, CommandSource commandSource, String[] strings, @Nullable BlockPos blockPos) {
	//#elseif MC>10710
	//$$ public List<String> getSuggestions(CommandSource commandSource, String[] strings, @Nullable BlockPos blockPos) {
	//#else
	//$$ public List<String> getSuggestions(CommandSource commandSource, String[] strings) {
	//#endif
	//$$     if (strings.length == 1) {
	//$$         return Arrays.asList("freeze", "rate", "warp");
	//$$     }
	//$$     if (strings.length == 2) {
	//$$ 		if ("freeze".equalsIgnoreCase(strings[0])) {
	//$$ 			return Arrays.asList("status", "deep", "on", "off");
	//$$ 		} else if ("rate".equalsIgnoreCase(strings[0])) {
	//$$ 			return Collections.singletonList("20.0");
	//$$ 		} else if ("warp".equalsIgnoreCase(strings[0])) {
	//$$ 			return Arrays.asList("3600", "72000");
	//$$		}
	//$$     }
	//$$     if (strings.length == 3) {
	//$$ 		if ("freeze".equalsIgnoreCase(strings[0])) {
	//$$ 			if ("on".equalsIgnoreCase(strings[1])) {
	//$$ 				return Collections.singletonList("deep");
	//$$ 			}
	//$$ 		}
	//$$     }
	//$$     return Collections.emptyList();
	//$$ }
	//#endif

	private static int setFreeze(
		//#if MC>=11300
		CommandSourceStack source,
		//#else
		//$$ CommandSource source,
		//#endif
		boolean isDeep, boolean freeze) {
		//#if MC>10809
		ServerTickRateManager trm = ((MinecraftServerTickRate)source.getServer()).getTickRateManager();
		//#else
		//$$ ServerTickRateManager trm = ((MinecraftServerTickRate)MinecraftServer.getInstance()).getTickRateManager();
		//#endif
		trm.setFrozenState(freeze, isDeep);
		if (trm.gameIsPaused()) {
			Messenger.m(source, "gi Game is "+(isDeep?"deeply ":"")+"frozen");
		} else {
			Messenger.m(source, "gi Game runs normally");
		}
		return 1;
	}

	private static int freezeStatus(
		//#if MC>=11300
		CommandSourceStack source
		//#else
		//$$ CommandSource source
		//#endif
	) {
		//#if MC>10809
		ServerTickRateManager trm = ((MinecraftServerTickRate)source.getServer()).getTickRateManager();
		//#else
		//$$ ServerTickRateManager trm = ((MinecraftServerTickRate)MinecraftServer.getInstance()).getTickRateManager();
		//#endif
		if (trm.gameIsPaused()) {
			Messenger.m(source, "gi Freeze Status: Game is "+(trm.deeplyFrozen()?"deeply ":"")+"frozen");
		} else {
			Messenger.m(source, "gi Freeze Status: Game runs normally");
		}
		return 1;
	}

	private static int toggleFreeze(
		//#if MC>=11300
		CommandSourceStack source,
		//#else
		//$$ CommandSource source,
		//#endif
		boolean isDeep) {
		//#if MC>10809
		ServerTickRateManager trm = ((MinecraftServerTickRate)source.getServer()).getTickRateManager();
		//#else
		//$$ ServerTickRateManager trm = ((MinecraftServerTickRate)MinecraftServer.getInstance()).getTickRateManager();
		//#endif
		return setFreeze(source, isDeep, !trm.gameIsPaused());
	}

	private static int queryTps(
		//#if MC>=11300
		CommandSourceStack source
		//#else
		//$$ CommandSource source
		//#endif
	) {
		//#if MC>10809
		ServerTickRateManager trm = ((MinecraftServerTickRate)source.getServer()).getTickRateManager();
		//#else
		//$$ ServerTickRateManager trm = ((MinecraftServerTickRate)MinecraftServer.getInstance()).getTickRateManager();
		//#endif
		Messenger.m(source, "w Current tps is: ",String.format("wb %.1f", trm.tickRate()));
		return (int) trm.tickRate();
	}

	private static int setTps(
		//#if MC>=11300
		CommandSourceStack source,
		//#else
		//$$ CommandSource source,
		//#endif
		float tps) {
		//#if MC>10809
		ServerTickRateManager trm = ((MinecraftServerTickRate)source.getServer()).getTickRateManager();
		//#else
		//$$ ServerTickRateManager trm = ((MinecraftServerTickRate)MinecraftServer.getInstance()).getTickRateManager();
		//#endif
		trm.setTickRate(tps, true);
		queryTps(source);
		return (int)tps;
	}

	private static int setWarp(
		//#if MC>=11300
		CommandSourceStack source,
		//#else
		//$$ CommandSource source,
		//#endif
		int advance, String tailCommand) {
		//#if MC>=11300
		Entity entity = source.getEntity();
		//#elseif MC>10710
		//$$ Entity entity = source.asEntity();
		//#endif
		if (!(
			//#if MC>10710
			entity
			//#else
			//$$ source
			//#endif
				instanceof ServerPlayerEntity)) {
			return 1;
		}
		//#if MC>10710
		ServerPlayerEntity player = (ServerPlayerEntity) entity;
		//#else
		//$$ ServerPlayerEntity player = (ServerPlayerEntity) source;
		//#endif

		//#if MC>10809
		ServerTickRateManager trm = ((MinecraftServerTickRate)source.getServer()).getTickRateManager();
		//#else
		//$$ ServerTickRateManager trm = ((MinecraftServerTickRate)MinecraftServer.getInstance()).getTickRateManager();
		//#endif
		Text msg = trm.requestGameToWarpSpeed(player, advance, tailCommand, source);
		Messenger.m(source, msg);
		return 1;
	}

	//#if MC>11202
	private static CompletableFuture<Suggestions> suggest(Iterable<String> iterable, SuggestionsBuilder suggestionsBuilder) {
		iterable.forEach(suggestionsBuilder::suggest);
		return suggestionsBuilder.buildFuture();
	}
	//#endif

	//#if MC<=10710
	//$$ @Override
	//$$ public int compareTo(@NotNull Object o) {
	//$$ 	return this.compareTo((Command) o);
	//$$ }
	//#endif
}
