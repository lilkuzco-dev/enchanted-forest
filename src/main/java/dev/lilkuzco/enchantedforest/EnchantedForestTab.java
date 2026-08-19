package dev.lilkuzco.enchantedforest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public final class EnchantedForestTab {
	public static final ResourceKey<CreativeModeTab> KEY =
			ResourceKey.create(Registries.CREATIVE_MODE_TAB, EnchantedForest.id("main"));

	public static List<Item> contents() {
		return new ArrayList<>(EnchantedForestItems.all().values());
	}

	public static void register() {
		CreativeModeTab tab = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
				.title(Component.translatable("itemGroup.enchanted_forest.main"))
				.icon(() -> new ItemStack(EnchantedForestItems.all().get("enchanted_heartwood")))
				.displayItems((parameters, output) -> contents().forEach(output::accept))
				.build();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, KEY, tab);
	}

	public static void assertComplete() {
		Set<Item> inTab = new HashSet<>(contents());
		List<String> problems = new ArrayList<>();
		for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
			Identifier id = entry.getKey().identifier();
			if (!id.getNamespace().equals(EnchantedForest.MOD_ID)) {
				continue;
			}
			Item item = entry.getValue().asItem();
			if (item == Items.AIR || !inTab.contains(item)) {
				problems.add(id.toString());
			}
		}
		if (!problems.isEmpty()) {
			throw new IllegalStateException("Enchanted Forest content unreachable in creative: " + problems);
		}
	}

	private EnchantedForestTab() {
	}
}
