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
//$$ import java.util.ArrayList;
//#endif
//#if MC<=10710
//$$ import net.minecraft.server.command.Command;
//$$ import org.jetbrains.annotations.NotNull;
//#endif
import net.minecraft.server.command.handler.CommandManager;
//#if MC>=11300
import net.minecraft.server.command.source.CommandSourceStack;
//#else
//$$ import net.minecraft.server.command.source.CommandSource;
//#endif

public class TickCommand
//#if MC<=11202
//$$ extends AbstractCommand
//#endif
{
	//#if MC>=11300
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(CommandManager.literal("tick").
			requires((p) -> CommandHelper.canUseCommand(p, CarpetSettings.commandTick)).
			then(CommandManager.literal("freeze").
				executes((c) -> toggleFreeze(c.getSource(), false))));
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
	//$$		return this.getName() + " <option> <value>";
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
	//$$ 	    	}
	//$$    	}
	//$$     }
	//$$
	//$$	@Override
	//#if MC>10809
	//$$   public boolean canUse(MinecraftServer minecraftServer, CommandSource commandSource) {
	//#else
	//$$   public boolean canUse(CommandSource commandSource) {
	//#endif
	//$$   return CommandHelper.canUseCommand(commandSource, CarpetSettings.carpetCommandPermissionLevel);
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
	//$$         return Collections.singletonList("freeze");
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

	//#if MC<=10710
	//$$ @Override
	//$$ public int compareTo(@NotNull Object o) {
	//$$ 	return this.compareTo((Command) o);
	//$$ }
	//#endif
}
