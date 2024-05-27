package com.kahzerx.carpet.mixins.commands.counter;

import com.kahzerx.carpet.CarpetSettings;
import com.kahzerx.carpet.helpers.HopperCounter;
import com.kahzerx.carpet.utils.WoolTool;
import net.minecraft.block.HopperBlock;
//#if MC<=10809
//$$ import net.minecraft.server.MinecraftServer;
//#endif
//#if MC>11202
import net.minecraft.block.entity.BlockEntityType;
//#endif
import net.minecraft.block.entity.HopperBlockEntity;
//#if MC>10809
import net.minecraft.block.entity.LootInventoryBlockEntity;
//#elseif MC>10710
//$$ import net.minecraft.block.entity.InventoryBlockEntity;
//#else
//$$ import net.minecraft.block.entity.BlockEntity;
//$$ import net.minecraft.util.math.BlockPos;
//$$ import net.minecraft.util.math.Directions;
//$$ import org.jetbrains.annotations.Nullable;
//#endif
import net.minecraft.item.ItemStack;
//#if MC>11002
import net.minecraft.util.DefaultedList;
//#endif
//#if MC>10710
import net.minecraft.item.DyeColor;
//#else
//$$ import com.kahzerx.carpet.utils.WoolTool.DyeColor;
//#endif
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
//#if MC>10809
public abstract class HopperBlockEntityMixin extends LootInventoryBlockEntity {
//#elseif MC>10710
//$$ public abstract class HopperBlockEntityMixin extends InventoryBlockEntity {
//#else
//$$ public abstract class HopperBlockEntityMixin extends BlockEntity {
//#endif
//#if MC>11002
@Shadow
private DefaultedList<ItemStack> inventory;

	@Shadow
	public abstract void setStack(int slot, @Nullable ItemStack stack);

	//#else
//$$ @Shadow
//$$ private ItemStack[] inventory;
//#endif
//#if MC<=10710
//$$ 	@Shadow
//$$	@Nullable
//$$	public abstract ItemStack getStack(int slot);
//$$ 	@Shadow
//$$	public abstract void setStack(int slot, @Nullable ItemStack stack);
//#endif
	//#if MC>11202
	protected HopperBlockEntityMixin(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}
	//#endif

	@Inject(method = "pushItems()Z", at = @At(value = "HEAD"), cancellable = true)
	private void onInsert(CallbackInfoReturnable<Boolean> cir) {
		if (CarpetSettings.hopperCounters) {
			//#if MC>11202
			DyeColor woolColor = WoolTool.getWoolColorAtPosition(this.world, this.pos.offset(this.getBlockState().get(HopperBlock.FACING)));
			//#elseif MC>10710
			//$$ DyeColor woolColor = WoolTool.getWoolColorAtPosition(this.world, this.pos.offset(HopperBlock.getFacing(this.getBlockMetadata())));
			//#else
			//$$ int dir = HopperBlock.getFacing(this.getBlockMetadata());
			//$$ DyeColor woolColor = WoolTool.getWoolColorAtPosition(this.world, new BlockPos(this.x + Directions.X_OFFSET[dir], this.y + Directions.Y_OFFSET[dir], this.z + Directions.Z_OFFSET[dir]));
			//#endif
			if (woolColor != null) {
				//#if MC>11002
				for (int i = 0; i < this.inventory.size(); i++) {
				//#else
				//$$ for (int i = 0; i < this.inventory.length; i++) {
				//#endif
					//#if MC>11002
					if (!this.getStack(i).isEmpty()) {
					//#else
					//$$ if (this.getStack(i) != null) {
					//#endif
						ItemStack itemStack = this.getStack(i).copy();
						//#if MC>10809
						HopperCounter.getCounter(woolColor).add(world.getServer(), itemStack);
						//#else
						//$$ HopperCounter.getCounter(woolColor).add(MinecraftServer.getInstance(), itemStack);
						//#endif
						//#if MC>11002
						this.setStack(i, ItemStack.EMPTY);
						//#else
						//$$ this.setStack(i, null);
						//#endif
					}
				}
				cir.setReturnValue(true);
			}
		}
	}
}
