package com.kahzerx.carpet.helpers;

import java.util.*;
import java.util.stream.Collectors;

import com.kahzerx.carpet.utils.Messenger;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
//#if MC>11202
import net.minecraft.world.dimension.DimensionType;
//#else
//$$ import net.minecraft.text.LiteralText;
//#endif
//#if MC>11102
import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
//#else
//$$ import java.util.LinkedHashMap;
//#endif
//#if MC>10710
import net.minecraft.item.DyeColor;
//#else
//$$ import com.kahzerx.carpet.utils.WoolTool.DyeColor;
//#endif
import net.minecraft.server.MinecraftServer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class HopperCounter {
	private static final Map<DyeColor, HopperCounter> COUNTERS;
	/**
	 * The counter's colour, determined by the colour of wool it's pointing into
	 */
	public final DyeColor color;

	private final String coloredName;

	/**
	 * The starting tick of the counter, used to calculate in-game time. Only initialised when the first item enters the
	 * counter
	 */
	private long startTick;
	/**
	 * The starting millisecond of the counter, used to calculate IRl time. Only initialised when the first item enters
	 * the counter
	 */
	private long startMillis;
	//#if MC>11102
	private final Object2LongMap<Item> counter = new Object2LongLinkedOpenHashMap<>();
	//#else
	//$$ private final Map<Item, Long> counter = new LinkedHashMap<>();
	//#endif

	private HopperCounter(DyeColor color) {
		startTick = -1;
		this.color = color;
		this.coloredName = color.name();
	}

	public void add(MinecraftServer server, ItemStack stack) {
		if (startTick < 0) {
			//#if MC>11202
			startTick = server.getWorld(DimensionType.OVERWORLD).getTime();
			//#else
			//$$ startTick = server.getSourceWorld().getTime();
			//#endif
			startMillis = System.currentTimeMillis();
		}
		Item item = stack.getItem();
		//#if MC>11102
		counter.put(item, counter.getLong(item)+stack.getSize());
		//#elseif MC>11002
		//$$ counter.put(item, counter.getOrDefault(item, 0L)+stack.getSize());
		//#else
		//$$ counter.put(item, counter.getOrDefault(item, 0L)+stack.size);
		//#endif
	}

	public void reset(MinecraftServer server) {
		counter.clear();
		//#if MC>11202
		startTick = server.getWorld(DimensionType.OVERWORLD).getTime();
		//#else
		//$$ startTick = server.getSourceWorld().getTime();
		//#endif
		startMillis = System.currentTimeMillis();
	}

	public static void resetAll(MinecraftServer server, boolean fresh) {
		for (HopperCounter counter : COUNTERS.values()) {
			counter.reset(server);
			if (fresh) {
				counter.startTick = -1;
			}
		}
	}

	public static HopperCounter getCounter(DyeColor color) {
		return COUNTERS.get(color);
	}

	public static List<Text> formatAll(MinecraftServer server, boolean realtime) {
		List<Text> text = new ArrayList<>();
		for (HopperCounter counter : COUNTERS.values()) {
			List<Text> temp = counter.format(server, realtime, false);
			if (temp.size() > 1) {
				if (!text.isEmpty()) {
					text.add(Messenger.s(""));
				}
				text.addAll(temp);
			}
		}
		if (text.isEmpty()) {
			text.add(Messenger.s("No items have been counted yet."));
		}
		return text;
	}

	public List<Text> format(MinecraftServer server, boolean realTime, boolean brief) {
		//#if MC>11202
		long ticks = Math.max(realTime ? (System.currentTimeMillis() - startMillis) / 50 : server.getWorld(DimensionType.OVERWORLD).getTime() - startTick, 1);
		//#else
		//$$ long ticks = Math.max(realTime ? (System.currentTimeMillis() - startMillis) / 50 : server.getSourceWorld().getTime() - startTick, 1);
		//#endif
		if (startTick < 0 || ticks == 0) {
			if (brief) {
				return Collections.singletonList(Messenger.c("b"+coloredName,"w : ","gi -, -/h, - min "));
			}
			return Collections.singletonList(Messenger.c(coloredName, "w  hasn't started counting yet"));
		}
		long total = getTotalItems();
		if (total == 0) {
			if (brief) {
				return Collections.singletonList(Messenger.c("b"+coloredName,"w : ","wb 0","w , ","wb 0","w /h, ", String.format("wb %.1f ", ticks / (20.0 * 60.0)), "w min"));
			}
			return Collections.singletonList(Messenger.c("w No items for ", coloredName, String.format("w  yet (%.2f min.%s)", ticks / (20.0 * 60.0), (realTime ? " - real time" : "")), "nb  [X]", "^g reset", "!/counter " + color.name() +" reset"));
		}
		if (brief) {
			return Collections.singletonList(Messenger.c("b"+coloredName,"w : ", "wb "+total,"w , ", "wb "+(total * (20 * 60 * 60) / ticks),"w /h, ", String.format("wb %.1f ", ticks / (20.0 * 60.0)), "w min"));
		}
		List<Text> items = new ArrayList<>();
		items.add(Messenger.c("w Items for ", coloredName, "w  (",String.format("wb %.2f", ticks*1.0/(20*60)), "w  min"+(realTime?" - real time":"")+"), ", "w total: ", "wb "+total, "w , (",String.format("wb %.1f",total*1.0*(20*60*60)/ticks),"w /h):", "nb [X]", "^g reset", "!/counter "+color+" reset"));
		items.addAll(counter.
			//#if MC>11102
			object2LongEntrySet()
			//#else
			//$$ entrySet()
			//#endif
			.stream().sorted((e, f) -> Long.compare(
				//#if MC>11102
				f.getLongValue(),
				//#else
				//$$ f.getValue(),
				//#endif
				//#if MC>11102
				e.getLongValue()
				//#else
				//$$ e.getValue()
				//#endif
			)).map(e -> {
			Item item = e.getKey();
			//#if MC>11202
			Text itemName = new TranslatableText(item.getTranslationKey());
			//#else
			//$$ Text itemName = new LiteralText(item.getName(new ItemStack(item)));
			//#endif
			//#if MC>11102
			long count = e.getLongValue();
			//#else
			//$$ long count = e.getValue();
			//#endif
			return Messenger.c("g - ", itemName, "g : ","wb "+count,"g , ", String.format("wb %.1f", count * (20.0 * 60.0 * 60.0) / ticks), "w /h");
		}).collect(Collectors.toList()));
		return items;
	}

	public long getTotalItems() {
		long sum = 0L;
		for (long val : counter.values()) {
			sum += val;
		}
		return counter.isEmpty() ? 0 : sum;
	}

	static {
		EnumMap<DyeColor, HopperCounter> counterMap = new EnumMap<>(DyeColor.class);
		for (DyeColor color : DyeColor.values()) {
			counterMap.put(color, new HopperCounter(color));
		}
		COUNTERS = Collections.unmodifiableMap(counterMap);
	}
}
