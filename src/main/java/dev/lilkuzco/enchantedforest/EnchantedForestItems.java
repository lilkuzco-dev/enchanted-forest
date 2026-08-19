package dev.lilkuzco.enchantedforest;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class EnchantedForestItems {
	private static final Map<String, Item> ALL = new LinkedHashMap<>();

	public static Map<String, Item> all() {
		return java.util.Collections.unmodifiableMap(ALL);
	}

	public static void register() {
		for (Map.Entry<String, Block> entry : EnchantedForestBlocks.all().entrySet()) {
			String path = entry.getKey();
			ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, EnchantedForest.id(path));
			Item item = new BlockItem(entry.getValue(),
					new Item.Properties().useBlockDescriptionPrefix()
							.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
							.setId(key));
			Registry.register(BuiltInRegistries.ITEM, key, item);
			ALL.put(path, item);
		}
	}

	private EnchantedForestItems() {
	}
}
