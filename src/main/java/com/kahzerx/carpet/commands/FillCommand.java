package com.kahzerx.carpet.commands;

//#if MC>10710
public class FillCommand {}  // TODO Missing translations
//#else
//$$ import com.google.common.collect.Lists;
//$$
//$$ import java.util.List;
//$$
//$$ import com.google.common.primitives.Doubles;
//$$ import com.kahzerx.carpet.CarpetSettings;
//$$ import com.kahzerx.carpet.utils.CommandHelper;
//$$ import net.minecraft.block.Block;
//$$ import net.minecraft.block.Blocks;
//$$ import net.minecraft.block.entity.BlockEntity;
//$$ import net.minecraft.inventory.Inventory;
//$$ import net.minecraft.nbt.NbtCompound;
//$$ import net.minecraft.nbt.SnbtParser;
//$$ import net.minecraft.server.command.AbstractCommand;
//$$ import net.minecraft.server.command.Command;
//$$ import net.minecraft.server.command.exception.CommandException;
//$$ import net.minecraft.server.command.exception.IncorrectUsageException;
//$$ import net.minecraft.server.command.exception.InvalidNumberException;
//$$ import net.minecraft.server.command.source.CommandSource;
//$$ import net.minecraft.util.math.BlockPos;
//$$ import net.minecraft.world.World;
//$$ import org.jetbrains.annotations.NotNull;
//$$
//$$ public class FillCommand extends AbstractCommand {
//$$ 	public FillCommand() {
//$$ 	}
//$$
//$$ 	public String getName() {
//$$ 		return "fill";
//$$ 	}
//$$
//$$ 	public int getRequiredPermissionLevel() {
//$$ 		return 2;
//$$ 	}
//$$
//$$ 	public String getUsage(CommandSource source) {
//$$ 		return "commands.fill.usage";
//$$ 	}
//$$
//$$ 	public void run(CommandSource source, String[] args) throws CommandException {
//$$ 		if (args.length < 7) {
//$$ 			throw new IncorrectUsageException("commands.fill.usage");
//$$ 		} else {
//$$ 			BlockPos blockPos = parseBlockPos(source, args, 0, false);
//$$ 			BlockPos blockPos2 = parseBlockPos(source, args, 3, false);
//$$ 			Block block = AbstractCommand.parseBlock(source, args[6]);
//$$ 			int i = 0;
//$$ 			if (args.length >= 8) {
//$$ 				i = parseInt(args[7], 0, 15);
//$$ 			}
//$$
//$$ 			BlockPos blockPos3 = new BlockPos(Math.min(blockPos.x, blockPos2.x), Math.min(blockPos.y, blockPos2.y), Math.min(blockPos.z, blockPos2.z));
//$$ 			BlockPos blockPos4 = new BlockPos(Math.max(blockPos.x, blockPos2.x), Math.max(blockPos.y, blockPos2.y), Math.max(blockPos.z, blockPos2.z));
//$$ 			int j = (blockPos4.x - blockPos3.x + 1) * (blockPos4.y - blockPos3.y + 1) * (blockPos4.z - blockPos3.z + 1);
//$$ 			if (j > CarpetSettings.fillLimit) {
//$$ 				throw new CommandException("commands.fill.tooManyBlocks", j, CarpetSettings.fillLimit);
//$$ 			} else if (blockPos3.y >= 0 && blockPos4.y < 256) {
//$$ 				World world = source.getSourceWorld();
//$$
//$$ 				for(int k = blockPos3.z; k < blockPos4.z + 16; k += 16) {
//$$ 					for(int l = blockPos3.x; l < blockPos4.x + 16; l += 16) {
//$$ 						if (!world.isChunkLoaded(l, blockPos4.y - blockPos3.y, k)) {
//$$ 							throw new CommandException("commands.fill.outOfWorld");
//$$ 						}
//$$ 					}
//$$ 				}
//$$
//$$ 				NbtCompound nbtCompound = new NbtCompound();
//$$ 				boolean bl = false;
//$$ 				if (args.length >= 10 && block.hasBlockEntity()) {
//$$ 					String string = parseText(source, args, 9).getString();
//$$ 					nbtCompound = (NbtCompound) SnbtParser.parse(string);
//$$ 					bl = true;
//$$ 				}
//$$
//$$ 				List<BlockPos> list = Lists.newArrayList();
//$$ 				j = 0;
//$$
//$$ 				for(int m = blockPos3.z; m <= blockPos4.z; ++m) {
//$$ 					for(int n = blockPos3.y; n <= blockPos4.y; ++n) {
//$$ 						for(int o = blockPos3.x; o <= blockPos4.x; ++o) {
//$$ 							BlockPos blockPos5 = new BlockPos(o, n, m);
//$$ 							if (args.length >= 9) {
//$$ 								if (!args[8].equals("outline") && !args[8].equals("hollow")) {
//$$ 									if (args[8].equals("destroy")) {
//$$ 										world.breakBlock(blockPos5.x, blockPos5.y, blockPos5.z, true);
//$$ 									} else if (args[8].equals("keep")) {
//$$ 										if (!world.isAir(blockPos5.x, blockPos5.y, blockPos5.z)) {
//$$ 											continue;
//$$ 										}
//$$ 									} else if (args[8].equals("replace") && !block.hasBlockEntity()) {
//$$ 										if (args.length > 9) {
//$$ 											Block block2 = AbstractCommand.parseBlock(source, args[9]);
//$$ 											if (world.getBlock(blockPos5.x, blockPos5.y, blockPos5.z) != block2) {
//$$ 												continue;
//$$ 											}
//$$ 										}
//$$
//$$ 										if (args.length > 10) {
//$$ 											int p = parseInt(args[10]);
//$$ 											int bm = world.getBlockMetadata(blockPos5.x, blockPos5.y, blockPos5.z);
//$$ 											if (bm != p) {
//$$ 												continue;
//$$ 											}
//$$ 										}
//$$ 									}
//$$ 								} else if (o != blockPos3.x && o != blockPos4.x && n != blockPos3.y && n != blockPos4.y && m != blockPos3.z && m != blockPos4.z) {
//$$ 									if (args[8].equals("hollow")) {
//$$ 										world.setBlockWithMetadata(blockPos5.x, blockPos5.y, blockPos5.z, Blocks.AIR, 0, 2);  // TODO not sure about the metadata thing
//$$ 										list.add(blockPos5);
//$$ 									}
//$$ 									continue;
//$$ 								}
//$$ 							}
//$$
//$$ 							BlockEntity blockEntity = world.getBlockEntity(blockPos5.x, blockPos5.y, blockPos5.z);
//$$ 							if (blockEntity != null) {
//$$ 								if (blockEntity instanceof Inventory) {
//$$ 									for (int slot = 0; slot < ((Inventory) blockEntity).getSize(); slot++) {
//$$ 										((Inventory) blockEntity).removeStackQuietly(slot);
//$$ 									}
//$$ 								}
//$$ 								// TODO in 1.8.9 is setBarrierBlock
//$$ 								world.setBlockWithMetadata(blockPos5.x, blockPos5.y, blockPos5.z, Blocks.AIR, 0, 2);
//$$ 							}
//$$
//$$ 							if (world.setBlockWithMetadata(blockPos5.x, blockPos5.y, blockPos5.z, block, i, 3)) {
//$$ 								list.add(blockPos5);
//$$ 								++j;
//$$ 								if (bl) {
//$$ 									BlockEntity blockEntity2 = world.getBlockEntity(blockPos5.x, blockPos5.y, blockPos5.z);
//$$ 									if (blockEntity2 != null) {
//$$ 										nbtCompound.putInt("x", blockPos5.x);
//$$ 										nbtCompound.putInt("y", blockPos5.y);
//$$ 										nbtCompound.putInt("z", blockPos5.z);
//$$ 										blockEntity2.readNbt(nbtCompound);
//$$ 									}
//$$ 								}
//$$ 							}
//$$ 						}
//$$ 					}
//$$ 				}
//$$
//$$ 				for (BlockPos b : list) {
//$$ 					Block blo = world.getBlock(b.x, b.y, b.z);
//$$ 					world.onBlockChanged(b.x, b.y, b.z, blo);
//$$ 				}
//$$
//$$ 				if (j <= 0) {
//$$ 					throw new CommandException("commands.fill.failed");
//$$ 				} else {
//$$ 					sendSuccess(source, this, "commands.fill.success", j);
//$$ 				}
//$$ 			} else {
//$$ 				throw new CommandException("commands.fill.outOfWorld");
//$$ 			}
//$$ 		}
//$$ 	}
//$$
//$$ 	@Override
//$$ 	public boolean canUse(CommandSource source) {
//$$ 		return CommandHelper.canUseCommand(source, CarpetSettings.commandFill);
//$$ 	}
//$$
//$$ 	public List<String> getSuggestions(CommandSource source, String[] args) {
//$$ 		if (args.length > 0 && args.length <= 3) {
//$$ 			return suggestCoordinate(args, 0);
//$$ 		} else if (args.length > 3 && args.length <= 6) {
//$$ 			return suggestCoordinate(args, 3);
//$$ 		} else if (args.length == 7) {
//$$ 			return suggestMatching(args, Block.REGISTRY.keySet());
//$$ 		} else if (args.length == 9) {
//$$ 			return suggestMatching(args, "replace", "destroy", "keep", "hollow", "outline");
//$$ 		} else {
//$$ 			return args.length == 10 && "replace".equals(args[8]) ? suggestMatching(args, Block.REGISTRY.keySet()) : null;
//$$ 		}
//$$ 	}
//$$
//$$ 	public static BlockPos parseBlockPos(CommandSource source, String[] args, int startIndex, boolean center) throws InvalidNumberException {
//$$ 		BlockPos blockPos = source.getSourceBlockPos();
//$$ 		return new BlockPos((int) parseCoordinate(blockPos.x, args[startIndex], -30000000, 30000000, center), (int) parseCoordinate(blockPos.y, args[startIndex + 1], 0, 256, false), (int) parseCoordinate(blockPos.z, args[startIndex + 2], -30000000, 30000000, center));
//$$ 	}
//$$
//$$ 	public static double parseCoordinate(double c, String s, int min, int max, boolean center) throws InvalidNumberException {
//$$ 		boolean bl = s.startsWith("~");
//$$ 		double d = bl ? c : 0.0;
//$$ 		if (!bl || s.length() > 1) {
//$$ 			boolean bl2 = s.contains(".");
//$$ 			if (bl) {
//$$ 				s = s.substring(1);
//$$ 			}
//$$
//$$ 			d += parseDouble(s);
//$$ 			if (!bl2 && !bl && center) {
//$$ 				d += 0.5;
//$$ 			}
//$$ 		}
//$$
//$$ 		if (min != 0 || max != 0) {
//$$ 			if (d < (double)min) {
//$$ 				throw new InvalidNumberException("commands.generic.double.tooSmall", d, min);
//$$ 			}
//$$
//$$ 			if (d > (double)max) {
//$$ 				throw new InvalidNumberException("commands.generic.double.tooBig", d, max);
//$$ 			}
//$$ 		}
//$$
//$$ 		return d;
//$$ 	}
//$$
//$$ 	public static double parseDouble(String s) throws InvalidNumberException {
//$$ 		try {
//$$ 			double d = Double.parseDouble(s);
//$$ 			if (!Doubles.isFinite(d)) {
//$$ 				throw new InvalidNumberException("commands.generic.num.invalid", s);
//$$ 			} else {
//$$ 				return d;
//$$ 			}
//$$ 		} catch (NumberFormatException var3) {
//$$ 			throw new InvalidNumberException("commands.generic.num.invalid", s);
//$$ 		}
//$$ 	}
//$$
//$$ 	public static int parseInt(String s, int min, int max) throws InvalidNumberException {
//$$ 		int i = parseInt(s);
//$$ 		if (i < min) {
//$$ 			throw new InvalidNumberException("commands.generic.num.tooSmall", i, min);
//$$ 		} else if (i > max) {
//$$ 			throw new InvalidNumberException("commands.generic.num.tooBig", i, max);
//$$ 		} else {
//$$ 			return i;
//$$ 		}
//$$ 	}
//$$
//$$ 	public static int parseInt(String s) throws InvalidNumberException {
//$$ 		try {
//$$ 			return Integer.parseInt(s);
//$$ 		} catch (NumberFormatException var2) {
//$$ 			throw new InvalidNumberException("commands.generic.num.invalid", s);
//$$ 		}
//$$ 	}
//$$
//$$ 	public static List<String> suggestCoordinate(String[] args, int index) {
//$$ 		int i = args.length - 1;
//$$ 		String string;
//$$ 		if (i == index) {
//$$ 			string = "~";
//$$ 		} else if (i == index + 1) {
//$$ 			string = "~";
//$$ 		} else {
//$$ 			if (i != index + 2) {
//$$ 				return null;
//$$ 			}
//$$
//$$ 			string = "~";
//$$ 		}
//$$
//$$ 		return Lists.newArrayList(string);
//$$ 	}
//$$
//$$ 	@Override
//$$ 	public int compareTo(@NotNull Object o) {
//$$ 		return this.compareTo((Command) o);
//$$ 	}
//$$ }
//#endif
