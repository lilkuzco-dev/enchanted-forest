package dev.lilkuzco.enchantedforest;

import dev.lilkuzco.enchantedforest.block.EnchantedHeartwoodBlock;
import dev.lilkuzco.enchantedforest.block.GlowingPlantBlock;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class EnchantedForestBlocks {
	private static final Map<String, Block> ALL = new LinkedHashMap<>();

	public static final EnchantedHeartwoodBlock ENCHANTED_HEARTWOOD = new EnchantedHeartwoodBlock(
			woodProperties("enchanted_heartwood").lightLevel(state -> 5));
	public static final Block ENCHANTED_LOG = new RotatedPillarBlock(woodProperties("enchanted_log"));
	public static final Block ENCHANTED_PLANKS = new Block(
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(2.0F, 3.0F)
					.sound(SoundType.WOOD).ignitedByLava().setId(blockKey("enchanted_planks")));
	public static final Block ENCHANTED_LEAVES = new UntintedParticleLeavesBlock(
			0.035F, ParticleTypes.GLOW,
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(0.2F)
					.randomTicks().sound(SoundType.GRASS).noOcclusion().lightLevel(state -> 3)
					.isValidSpawn((state, level, pos, type) -> false)
					.isSuffocating((state, level, pos) -> false)
					.isViewBlocking((state, level, pos) -> false)
					.ignitedByLava().pushReaction(PushReaction.DESTROY)
					.isRedstoneConductor((state, level, pos) -> false)
					.setId(blockKey("enchanted_leaves")));

	public static final TreeGrower ENCHANTED_TREE_GROWER = new TreeGrower(
			"enchanted_forest_tree", java.util.Optional.empty(),
			java.util.Optional.of(ResourceKey.create(
					Registries.CONFIGURED_FEATURE, EnchantedForest.id("enchanted_tree"))),
			java.util.Optional.empty());
	public static final Block ENCHANTED_SAPLING = new SaplingBlock(
			ENCHANTED_TREE_GROWER,
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).noCollision()
					.randomTicks().instabreak().sound(SoundType.GRASS)
					.pushReaction(PushReaction.DESTROY).setId(blockKey("enchanted_sapling")));

	public static final Block STARFLOWER = plant("starflower", MapColor.COLOR_LIGHT_BLUE, 12);
	public static final Block FAIRY_BLOOM = plant("fairy_bloom", MapColor.COLOR_PURPLE, 15);
	public static final Block CRYSTAL_MOSS = plant("crystal_moss", MapColor.COLOR_CYAN, 8);

	public static Map<String, Block> all() {
		return java.util.Collections.unmodifiableMap(ALL);
	}

	public static void register() {
		register("enchanted_heartwood", ENCHANTED_HEARTWOOD);
		register("enchanted_log", ENCHANTED_LOG);
		register("enchanted_planks", ENCHANTED_PLANKS);
		register("enchanted_leaves", ENCHANTED_LEAVES);
		register("enchanted_sapling", ENCHANTED_SAPLING);
		register("starflower", STARFLOWER);
		register("fairy_bloom", FAIRY_BLOOM);
		register("crystal_moss", CRYSTAL_MOSS);
	}

	public static void registerInteractions() {
		FlammableBlockRegistry fire = FlammableBlockRegistry.getDefaultInstance();
		fire.add(ENCHANTED_HEARTWOOD, 5, 5);
		fire.add(ENCHANTED_LOG, 5, 5);
		fire.add(ENCHANTED_PLANKS, 5, 20);
		fire.add(ENCHANTED_LEAVES, 30, 60);
	}

	private static BlockBehaviour.Properties woodProperties(String id) {
		return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN)
				.instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASS)
				.strength(2.0F).sound(SoundType.WOOD).ignitedByLava().setId(blockKey(id));
	}

	private static Block plant(String id, MapColor color, int light) {
		return new GlowingPlantBlock(BlockBehaviour.Properties.of().mapColor(color).noCollision()
				.instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ)
				.lightLevel(state -> light).pushReaction(PushReaction.DESTROY).setId(blockKey(id)));
	}

	private static void register(String id, Block block) {
		Registry.register(BuiltInRegistries.BLOCK, blockKey(id), block);
		ALL.put(id, block);
	}

	private static ResourceKey<Block> blockKey(String id) {
		return ResourceKey.create(Registries.BLOCK, EnchantedForest.id(id));
	}

	private EnchantedForestBlocks() {
	}
}
