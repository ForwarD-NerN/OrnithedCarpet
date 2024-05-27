package com.kahzerx.carpet.utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
//#if MC>10710
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.item.DyeColor;
//#else
//$$ import net.minecraft.block.material.MapColor;
//#endif
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//#if MC<=11202
//$$ import net.minecraft.block.ColoredBlock;
//#endif

public class WoolTool {
	//#if MC>11202
	private static final Map<Block, DyeColor> BLOCK_TO_COLOR = Stream.of(new Object[][] {
		{ Blocks.WHITE_WOOL, DyeColor.WHITE },
		{ Blocks.ORANGE_WOOL, DyeColor.ORANGE },
		{ Blocks.MAGENTA_WOOL, DyeColor.MAGENTA },
		{ Blocks.LIGHT_BLUE_WOOL, DyeColor.LIGHT_BLUE },
		{ Blocks.YELLOW_WOOL, DyeColor.YELLOW },
		{ Blocks.LIME_WOOL, DyeColor.LIME },
		{ Blocks.PINK_WOOL, DyeColor.PINK },
		{ Blocks.GRAY_WOOL, DyeColor.GRAY },
		{ Blocks.LIGHT_GRAY_WOOL, DyeColor.SILVER },
		{ Blocks.CYAN_WOOL, DyeColor.CYAN },
		{ Blocks.PURPLE_WOOL, DyeColor.PURPLE },
		{ Blocks.BLUE_WOOL, DyeColor.BLUE },
		{ Blocks.BROWN_WOOL, DyeColor.BROWN },
		{ Blocks.GREEN_WOOL, DyeColor.GREEN },
		{ Blocks.RED_WOOL, DyeColor.RED },
		{ Blocks.BLACK_WOOL, DyeColor.BLACK },
	}).collect(Collectors.toMap(data -> (Block) data[0], data -> (DyeColor) data[1]));
	//#endif
	//#if MC<=10710
//$$	private static final Map<MapColor, DyeColor> BLOCK_TO_COLOR = Stream.of(new Object[][] {
//$$		{ MapColor.WHITE, DyeColor.WHITE },
//$$		{ MapColor.ORANGE, DyeColor.ORANGE },
//$$		{ MapColor.MAGENTA, DyeColor.MAGENTA },
//$$		{ MapColor.LIGHT_BLUE, DyeColor.LIGHT_BLUE },
//$$		{ MapColor.YELLOW, DyeColor.YELLOW },
//$$		{ MapColor.LIME, DyeColor.LIME },
//$$		{ MapColor.PINK, DyeColor.PINK },
//$$		{ MapColor.GRAY, DyeColor.GRAY },
//$$		{ MapColor.LIGHT_GRAY, DyeColor.SILVER },
//$$		{ MapColor.CYAN, DyeColor.CYAN },
//$$		{ MapColor.PURPLE, DyeColor.PURPLE },
//$$		{ MapColor.BLUE, DyeColor.BLUE },
//$$		{ MapColor.BROWN, DyeColor.BROWN },
//$$		{ MapColor.GREEN, DyeColor.GREEN },
//$$		{ MapColor.RED, DyeColor.RED },
//$$		{ MapColor.BLACK, DyeColor.BLACK },
//$$	}).collect(Collectors.toMap(data -> (MapColor) data[0], data -> (DyeColor) data[1]));
	//#endif

	//#if MC>10710
	public static DyeColor getWoolColorAtPosition(World world, BlockPos pos) {
	//#else
	//$$ public static DyeColor getWoolColorAtPosition(World world, BlockPos pos) {
	//#endif
		//TODO validate wool
		//#if MC>10710
		BlockState state = world.getBlockState(pos);
		//#else
		//$$ Block block = world.getBlock(pos.x, pos.y, pos.z);
		//#endif
		//#if MC>11202
		return BLOCK_TO_COLOR.get(state.getBlock());
		//#elseif MC>10710
		//$$ if (state.getBlock() != Blocks.WOOL) {
		//$$    return null;
		//$$ }
		//$$ return state.get(ColoredBlock.COLOR);
		//#else
		//$$ if (block != Blocks.WOOL) {
		//$$ 	return null;
		//$$ }
		//$$ return BLOCK_TO_COLOR.get(MapColor.forDyeColor(world.getBlockMetadata(pos.x, pos.y, pos.z)));
		//#endif
	}
	//#if MC<=10710
	//$$	public enum DyeColor {
	//$$		WHITE(0, "white", MapColor.WHITE),
	//$$		ORANGE(1, "orange", MapColor.ORANGE),
	//$$		MAGENTA(2, "magenta", MapColor.MAGENTA),
	//$$		LIGHT_BLUE(3, "light_blue", MapColor.LIGHT_BLUE),
	//$$		YELLOW(4, "yellow", MapColor.YELLOW),
	//$$		LIME(5, "lime", MapColor.LIME),
	//$$		PINK(6, "pink", MapColor.PINK),
	//$$		GRAY(7, "gray", MapColor.GRAY),
	//$$		SILVER(8, "light_gray", MapColor.LIGHT_GRAY),
	//$$		CYAN(9, "cyan", MapColor.CYAN),
	//$$		PURPLE(10, "purple", MapColor.PURPLE),
	//$$		BLUE(11, "blue", MapColor.BLUE),
	//$$		BROWN(12, "brown", MapColor.BROWN),
	//$$		GREEN(13, "green", MapColor.GREEN),
	//$$		RED(14, "red", MapColor.RED),
	//$$		BLACK(15, "black", MapColor.BLACK);
	//$$
	//$$		private static final DyeColor[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(DyeColor::getId)).toArray(DyeColor[]::new);
	//$$
	//$$		private final int id;
	//$$		private final String key;
	//$$		private final MapColor mapColor;
	//$$
	//$$		DyeColor(int id, String key, MapColor mapColor) {
	//$$			this.id = id;
	//$$			this.key = key;
	//$$			this.mapColor = mapColor;
	//$$		}
	//$$
	//$$		public static DyeColor byId(int id) {
	//$$			if (id < 0 || id >= BY_ID.length) {
	//$$				return null;
	//$$			}
	//$$
	//$$			return BY_ID[id];
	//$$		}
	//$$
	//$$		public int getId() {
	//$$			return id;
	//$$		}
	//$$
	//$$		public String getKey() {
	//$$			return key;
	//$$		}
	//$$
	//$$ 		public String serializeToString() {
	//$$ 			return key;
	//$$ 		}
	//$$
	//$$		public MapColor getMapColor() {
	//$$			return mapColor;
	//$$		}
	//$$	}
	//#endif
}
