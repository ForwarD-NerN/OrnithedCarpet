package com.kahzerx.carpet.commands;

import com.kahzerx.carpet.helpers.HopperCounter;
import com.kahzerx.carpet.utils.Messenger;
import com.kahzerx.carpet.CarpetSettings;
import com.kahzerx.carpet.utils.CommandHelper;
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
//#if MC>=11300
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.handler.CommandManager;
import net.minecraft.server.command.source.CommandSourceStack;
//#else
//$$ import net.minecraft.server.command.source.CommandSource;
//#endif
//#if MC>10710
import net.minecraft.item.DyeColor;
//#else
//$$ import com.kahzerx.carpet.utils.WoolTool.DyeColor;
//#endif
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CounterCommand
//#if MC<=11202
//$$ extends AbstractCommand
//#endif
{
	//#if MC>=11300
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> command = CommandManager.literal("counter").
			requires((p) -> CommandHelper.canUseCommand(p, CarpetSettings.hopperCounters)).
			executes((c) -> listAllCounters(c.getSource(), false)).
			then(CommandManager.literal("reset").
				executes((c) -> resetCounters(c.getSource())));
		for (DyeColor color : DyeColor.values()) {
			command.
				then(CommandManager.literal(color.serializeToString()).
					executes((c) -> displayCounter(c.getSource(), color, false)).
					then(CommandManager.literal("reset").
						executes((c) -> resetCounter(c.getSource(), color))).
					then(CommandManager.literal("realtime").
						executes((c) -> displayCounter(c.getSource(), color, true))));
		}
		dispatcher.register(command);
	}
	//#endif
	//#if MC<=11202
	//$$	@Override
	//$$	public String getName() {
	//$$		return "counter";
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
	//$$ 	     if (strings.length == 0) {
	//$$ 		    listAllCounters(commandSource, false);
	//$$    	 }
	//$$ 	     if (strings.length == 1) {
	//$$ 		    if ("reset".equalsIgnoreCase(strings[0])) {
	//$$ 			    resetCounters(commandSource);
	//$$ 	    	}
	//$$ 			for (DyeColor color : DyeColor.values()) {
	//$$				if (color.serializeToString().equalsIgnoreCase(strings[0])) {
	//$$					displayCounter(commandSource, color, false);
	//$$				}
	//$$			}
	//$$    	 }
	//$$			if (strings.length == 2) {
	//$$				for (DyeColor color : DyeColor.values()) {
	//$$					if (color.serializeToString().equalsIgnoreCase(strings[0])) {
	//$$						if ("reset".equalsIgnoreCase(strings[1])) {
	//$$							resetCounter(commandSource, color);
	//$$						}
	//$$						if ("realtime".equalsIgnoreCase(strings[1])) {
	//$$							displayCounter(commandSource, color, true);
	//$$						}
	//$$					}
	//$$				}
	//$$			}
	//$$     }
	//$$
	//$$	@Override
	//#if MC>10809
	//$$   public boolean canUse(MinecraftServer minecraftServer, CommandSource commandSource) {
	//#else
	//$$   public boolean canUse(CommandSource commandSource) {
	//#endif
	//$$   return CommandHelper.canUseCommand(commandSource, CarpetSettings.hopperCounters);
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
	//$$	if (strings.length == 1) {
	//$$		List<String> opts = new ArrayList<>();
	//$$		for (DyeColor color : DyeColor.values()) {
	//$$			opts.add(color.serializeToString().toLowerCase());
	//$$		}
	//$$		opts.add("reset");
	//$$		return opts;
	//$$	}
	//$$	if (strings.length == 2) {
	//$$		for (DyeColor color : DyeColor.values()) {
	//$$			if (color.serializeToString().equalsIgnoreCase(strings[0])) {
	//$$				return Arrays.asList("reset", "realtime");
	//$$			}
	//$$		}
	//$$ 	}
	//$$     return Collections.emptyList();
	//$$ }
	//#endif

	private static int displayCounter(
		//#if MC>=11300
		CommandSourceStack source,
		//#else
		//$$ CommandSource source,
		//#endif
		DyeColor color, boolean realtime) {
		HopperCounter counter = HopperCounter.getCounter(color);
		for (Text message : counter.format(
			//#if MC>10809
			source.getServer(),
			//#else
			//$$ MinecraftServer.getInstance(),
			//#endif
			realtime, false)) {
			Messenger.m(source, message);
		}
		return 1;
	}

	private static int resetCounters(
		//#if MC>=11300
		CommandSourceStack source
		//#else
		//$$ CommandSource source
		//#endif
	) {
		HopperCounter.resetAll(
			//#if MC>10809
			source.getServer(),
			//#else
			//$$ MinecraftServer.getInstance(),
			//#endif
			false);
		Messenger.m(source, "w Restarted all counters");
		return 1;
	}

	private static int resetCounter(
		//#if MC>=11300
		CommandSourceStack source,
		//#else
		//$$ CommandSource source,
		//#endif
		DyeColor color) {
		HopperCounter.getCounter(color).reset(
			//#if MC>10809
			source.getServer()
			//#else
			//$$ MinecraftServer.getInstance()
			//#endif
		);
		Messenger.m(source, "w Restarted " + color + " counter");
		return 1;
	}

	private static int listAllCounters(
		//#if MC>=11300
		CommandSourceStack source,
		//#else
		//$$ CommandSource source,
		//#endif
		boolean realtime) {
		for (Text message : HopperCounter.formatAll(
			//#if MC>10809
			source.getServer(),
			//#else
			//$$ MinecraftServer.getInstance(),
			//#endif
			realtime)) {
			Messenger.m(source, message);
		}
		return 1;
	}

	//#if MC<=10710
	//$$ @Override
	//$$ public int compareTo(@NotNull Object o) {
	//$$ 	return this.compareTo((Command) o);
	//$$ }
	//#endif
}
