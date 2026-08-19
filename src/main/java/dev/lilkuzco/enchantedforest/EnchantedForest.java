package dev.lilkuzco.enchantedforest;

import dev.lilkuzco.enchantedforest.worldgen.EnchantedForestWorldgen;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EnchantedForest implements ModInitializer {
	public static final String MOD_ID = "enchanted_forest";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		EnchantedForestBlocks.register();
		EnchantedForestBlocks.registerInteractions();
		EnchantedForestBlockEntities.register();
		EnchantedForestItems.register();
		EnchantedForestTab.register();
		EnchantedForestWorldgen.register();
		EnchantedForestTab.assertComplete();
		LOGGER.info("Enchanted Forest initialised: {} blocks, biome {}.",
				EnchantedForestBlocks.all().size(), EnchantedForestWorldgen.ENCHANTED_FOREST.identifier());
	}
}
