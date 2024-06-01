package com.kahzerx.carpet.mixins.rule.emeraldOreUpdateSuppressor;

import com.kahzerx.carpet.CarpetSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.OreBlock;
//#if MC<=11202
//$$ import net.minecraft.block.material.MapColor;
//$$ import net.minecraft.block.material.Material;
//#endif
//#if MC>10710
import net.minecraft.block.state.BlockState;
//#endif
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(OreBlock.class)
public class OreBlockMixin extends Block {
	//#if MC>11202
	public OreBlockMixin(Properties properties) {
		super(properties);
	}
	//#elseif MC>10809
	//$$	public OreBlockMixin(Material material, MapColor mapColor) {
	//$$		super(material, mapColor);
	//$$	}
	//#else
	//$$	public OreBlockMixin(Material material) {
	//$$		super(material);
	//$$	}
	//#endif

	@Override
	//#if MC>11002
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
	//#elseif MC>10809
	//$$ public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock) {
	//#elseif MC>10710
	//$$ public void neighborChanged(World world, BlockPos pos, BlockState state, Block neighborBlock) {
	//#else
	//$$ public void neighborChanged(World world, int x, int y, int z, Block neighborBlock) {
	//#endif
		if (CarpetSettings.emeraldOreUpdateSuppressor && this == Blocks.EMERALD_ORE) {
			throw new StackOverflowError();
		}
	}
}
