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

/** Claims flat/gently rolling birch-forest climates without touching Waldschatten's terrain. */
public final class EnchantedForestWorldgen {
	public static final ResourceKey<Biome> ENCHANTED_FOREST =
			ResourceKey.create(Registries.BIOME, EnchantedForest.id("enchanted_forest"));
	public static final Feature<NoneFeatureConfiguration> ENCHANTED_TREE = new EnchantedTreeFeature();

	private static final long LOWLAND_MIN = Climate.quantizeCoord(0.05F);
	private static final long LOWLAND_MAX = Climate.quantizeCoord(1.0F);
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
		int slices = 0;
		for (Pair<Climate.ParameterPoint, Holder<Biome>> entry : original.values()) {
			if (!isBirchForest(entry.getSecond())) {
				claimed.add(entry);
				continue;
			}
			Climate.ParameterPoint point = entry.getFirst();
			long min = Math.max(point.erosion().min(), LOWLAND_MIN);
			long max = Math.min(point.erosion().max(), LOWLAND_MAX);
			if (min > max) {
				claimed.add(entry);
				continue;
			}
			if (point.erosion().min() < min) {
				claimed.add(Pair.of(withErosion(point, point.erosion().min(), min - 1), entry.getSecond()));
			}
			claimed.add(Pair.of(withErosion(point, min, max), ours.get()));
			slices++;
			if (max < point.erosion().max()) {
				claimed.add(Pair.of(withErosion(point, max + 1, point.erosion().max()), entry.getSecond()));
			}
		}

		if (slices > 0) {
			EnchantedForest.LOGGER.info(
					"Enchanted Forest claimed {} flat/gentle birch-forest climate slices.", slices);
			return new Climate.ParameterList<>(claimed);
		}
		return original;
	}

	private static boolean isBirchForest(Holder<Biome> biome) {
		return biome.is(Biomes.BIRCH_FOREST) || biome.is(Biomes.OLD_GROWTH_BIRCH_FOREST);
	}

	private static Climate.ParameterPoint withErosion(Climate.ParameterPoint point, long min, long max) {
		return new Climate.ParameterPoint(point.temperature(), point.humidity(), point.continentalness(),
				new Climate.Parameter(min, max), point.depth(), point.weirdness(), point.offset());
	}

	public static void register() {
		Registry.register(BuiltInRegistries.FEATURE,
				EnchantedForest.id("enchanted_tree"), ENCHANTED_TREE);
		EnchantedForest.LOGGER.info(
				"Enchanted Forest will claim flat/gentle birch-forest slices from multi-noise sources.");
	}

	private EnchantedForestWorldgen() {
	}
}
