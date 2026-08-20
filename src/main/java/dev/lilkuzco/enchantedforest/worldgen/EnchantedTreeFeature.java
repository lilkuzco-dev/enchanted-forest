package dev.lilkuzco.enchantedforest.worldgen;

import dev.lilkuzco.enchantedforest.EnchantedForestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** A vanilla-oak-scale luminous tree whose base is the single render anchor for its glint. */
public final class EnchantedTreeFeature extends Feature<NoneFeatureConfiguration> {
	public static final int TRUNK_HEIGHT = 5;
	public static final int TOTAL_HEIGHT = 7;
	private static final int CANOPY_BASE_Y = 3;
	private static final int[] CANOPY_RADII = {2, 2, 2, 1};

	public EnchantedTreeFeature() {
		super(NoneFeatureConfiguration.CODEC);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		BlockPos origin = context.origin();
		RandomSource random = context.random();
		BlockState soil = level.getBlockState(origin.below());
		// In 26.2 grass blocks are no longer members of #minecraft:dirt. Since
		// the heightmap normally places forest features directly above grass,
		// checking only that tag rejects almost every configured tree attempt.
		if ((!soil.is(Blocks.GRASS_BLOCK) && !soil.is(BlockTags.DIRT)) || !hasRoom(level, origin)) {
			return false;
		}

		level.setBlock(origin, EnchantedForestBlocks.ENCHANTED_HEARTWOOD.defaultBlockState(), 2);
		for (int y = 1; y < TRUNK_HEIGHT; y++) {
			level.setBlock(origin.above(y), EnchantedForestBlocks.ENCHANTED_LOG.defaultBlockState(), 2);
		}

		placeCanopy(level, origin, random);
		placeGlowBerryVines(level, origin, random);
		placeSporeBlossoms(level, origin);
		return true;
	}

	private static boolean hasRoom(WorldGenLevel level, BlockPos origin) {
		// Keep trunks at least three blocks apart, but allow neighboring canopies to
		// merge. Rejecting every existing leaf made most of the configured attempts
		// fail and turned an 8-attempt "forest" into a few isolated plains trees.
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				for (int y = -2; y <= TOTAL_HEIGHT; y++) {
					BlockState nearby = level.getBlockState(origin.offset(x, y, z));
					if (nearby.is(EnchantedForestBlocks.ENCHANTED_HEARTWOOD)
							|| nearby.is(EnchantedForestBlocks.ENCHANTED_LOG)) {
						return false;
					}
				}
			}
		}
		for (int y = 0; y < TOTAL_HEIGHT; y++) {
			int radius = y < CANOPY_BASE_Y ? 0 : 2;
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					BlockState state = level.getBlockState(origin.offset(x, y, z));
					if (!state.isAir() && !state.is(BlockTags.REPLACEABLE_BY_TREES)
							&& !state.is(EnchantedForestBlocks.ENCHANTED_LEAVES)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private static void placeCanopy(WorldGenLevel level, BlockPos origin, RandomSource random) {
		for (int layer = 0; layer < CANOPY_RADII.length; layer++) {
			int y = CANOPY_BASE_Y + layer;
			int radius = CANOPY_RADII[layer];
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					if (x == 0 && z == 0 && y < TRUNK_HEIGHT) {
						continue;
					}
					int distanceSquared = x * x + z * z;
					if (distanceSquared > radius * radius + 1) {
						continue;
					}
					if (distanceSquared > radius * radius - 1 && random.nextFloat() < 0.25F) {
						continue;
					}
					int logDistance = Math.min(7, Math.max(1, Math.abs(x) + Math.abs(z)
							+ Math.max(0, y - (TRUNK_HEIGHT - 1))));
					BlockState leaves = EnchantedForestBlocks.ENCHANTED_LEAVES.defaultBlockState()
							.setValue(LeavesBlock.DISTANCE, logDistance)
							.setValue(LeavesBlock.PERSISTENT, false);
					BlockPos pos = origin.offset(x, y, z);
					if (level.getBlockState(pos).isAir()) {
						level.setBlock(pos, leaves, 2);
					}
				}
			}
		}
	}

	private static void placeGlowBerryVines(WorldGenLevel level, BlockPos origin, RandomSource random) {
		int[][] anchors = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}, {2, 1}, {-1, -2}};
		for (int[] anchor : anchors) {
			BlockPos top = origin.offset(anchor[0], CANOPY_BASE_Y - 1, anchor[1]);
			if (!level.getBlockState(top).isAir()
					|| !level.getBlockState(top.above()).is(EnchantedForestBlocks.ENCHANTED_LEAVES)) {
				continue;
			}
			int length = 1 + random.nextInt(3);
			for (int i = 0; i < length; i++) {
				BlockPos pos = top.below(i);
				if (!level.getBlockState(pos).isAir()) {
					break;
				}
				boolean head = i == length - 1 || !level.getBlockState(pos.below()).isAir();
				BlockState vine = (head ? Blocks.CAVE_VINES : Blocks.CAVE_VINES_PLANT)
						.defaultBlockState().setValue(CaveVines.BERRIES, random.nextFloat() < 0.72F);
				level.setBlock(pos, vine, 2);
				if (head) {
					break;
				}
			}
		}
	}

	private static void placeSporeBlossoms(WorldGenLevel level, BlockPos origin) {
		for (int[] offset : new int[][] {{1, CANOPY_BASE_Y - 1, 1},
				{-1, CANOPY_BASE_Y - 1, -1}, {1, CANOPY_BASE_Y - 1, -1}}) {
			BlockPos pos = origin.offset(offset[0], offset[1], offset[2]);
			if (level.getBlockState(pos).isAir()) {
				level.setBlock(pos, Blocks.SPORE_BLOSSOM.defaultBlockState(), 2);
			}
		}
	}
}
