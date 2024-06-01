package com.kahzerx.carpet.mixins.rule.fillLimit;

import org.spongepowered.asm.mixin.Mixin;
//#if MC>10710
import com.kahzerx.carpet.CarpetSettings;
import net.minecraft.server.command.FillCommand;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FillCommand.class)
public class FillCommandMixin {
	@ModifyConstant(method =
		//#if MC>11202
		"fillBlocks",
		//#else
		//$$ "run",
		//#endif
		constant = @Constant(intValue = 32768))
	private
	//#if MC>11202
	static
	//#endif
	int limit(int original) {
		return CarpetSettings.fillLimit;
	}
}
//#else
//$$ import net.minecraft.server.command.handler.CommandManager;
//$$ @Mixin(CommandManager.class)
//$$ public class FillCommandMixin {}
//#endif
