package dev.lilkuzco.enchantedforest.worldgen;

import com.mojang.datafixers.util.Pair;
import dev.lilkuzco.enchantedforest.EnchantedForest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Replaces complete birch-forest climate entries so enchanted forests generate at full biome scale. */
public final class EnchantedForestWorldgen {
	public static final ResourceKey<Biome> ENCHANTED_FOREST =
			ResourceKey.create(Registries.BIOME, EnchantedForest.id("enchanted_forest"));
	public static final Feature<NoneFeatureConfiguration> ENCHANTED_TREE = new EnchantedTreeFeature();

	private static volatile HolderGetter<Biome> biomeLookup;

	public static void rememberBiomeLookup(HolderGetter<Biome> biomes) {
		biomeLookup = biomes;
	}

	public static Climate.ParameterList<Holder<Biome>> claimIn(
			Climate.ParameterList<Holder<Biome>> original) {
		HolderGetter<Biome> lookup = biomeLookup;
		if (lookup == null) {
			return original;
		}
		Optional<Holder.Reference<Biome>> ours = lookup.get(ENCHANTED_FOREST);
		if (ours.isEmpty()) {
			return original;
		}

		List<Pair<Climate.ParameterPoint, Holder<Biome>>> claimed = new ArrayList<>();
		int entries = 0;
		for (Pair<Climate.ParameterPoint, Holder<Biome>> entry : original.values()) {
			if (isBirchForest(entry.getSecond())) {
				claimed.add(Pair.of(entry.getFirst(), ours.get()));
				entries++;
			} else {
				claimed.add(entry);
			}
		}

		if (entries > 0) {
			EnchantedForest.LOGGER.info(
					"Enchanted Forest claimed {} complete birch-forest climate entries.", entries);
			return new Climate.ParameterList<>(claimed);
		}
		return original;
	}

	private static boolean isBirchForest(Holder<Biome> biome) {
		return biome.is(Biomes.BIRCH_FOREST) || biome.is(Biomes.OLD_GROWTH_BIRCH_FOREST);
	}

	public static void register() {
		Registry.register(BuiltInRegistries.FEATURE,
				EnchantedForest.id("enchanted_tree"), ENCHANTED_TREE);
		EnchantedForest.LOGGER.info(
				"Enchanted Forest will claim complete birch-forest entries from multi-noise sources.");
	}

	private EnchantedForestWorldgen() {
	}
}
