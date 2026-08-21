package dev.lilkuzco.enchantedforest.client;

import dev.lilkuzco.enchantedforest.EnchantedForest;
import dev.lilkuzco.enchantedforest.EnchantedForestBlocks;
import dev.lilkuzco.enchantedforest.EnchantedForestEntities;
import dev.lilkuzco.enchantedforest.worldgen.EnchantedForestWorldgen;
import dev.lilkuzco.enchantedforest.worldgen.EnchantedTreeFeature;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;

/** Remote render and runtime battery; never invoked by a normal client. */
public final class EnchantedForestRenderTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		context.runOnClient(client -> client.options.renderDistance().set(10));
		try (TestSingleplayerContext world = context.worldBuilder().create()) {
			context.waitTicks(80);
			TestServerContext server = world.getServer();
			server.runCommand("gamerule advance_time false");
			server.runCommand("gamerule advance_weather false");
			server.runCommand("gamerule max_block_modifications 1000000");
			server.runCommand("difficulty peaceful");
			server.runCommand("gamemode creative @p");
			server.runCommand("time set noon");
			server.runCommand("kill @e[type=!minecraft:player]");
			server.runCommand("execute at @p run fill ~-18 ~-1 ~-18 ~18 ~-1 ~18 minecraft:grass_block");
			server.runCommand("execute at @p run fill ~-18 ~ ~-18 ~18 ~16 ~18 minecraft:air");
			// A full grove makes both palette consistency and actual forest density
			// visible in one frame; a single showcase tree cannot catch a plains biome.
			for (int x : new int[] {-8, -4, 0, 4, 8}) {
				for (int z : new int[] {4, 9, 14}) {
					server.runCommand("execute at @p run place feature enchanted_forest:enchanted_tree ~"
							+ x + " ~ ~" + z);
				}
			}
			server.runCommand("execute at @p run summon enchanted_forest:enchanted_bird ~-3 ~3 ~3 {NoAI:1b,PersistenceRequired:1b}");
			server.runCommand("execute at @p run summon enchanted_forest:enchanted_fox ~0 ~ ~4 {NoAI:1b,PersistenceRequired:1b}");
			server.runCommand("execute at @p run summon enchanted_forest:enchanted_bear ~-4 ~ ~6 {NoAI:1b,PersistenceRequired:1b}");
			context.waitTicks(40);

			server.runOnServer(minecraftServer -> auditRuntime(minecraftServer.overworld()));
			context.waitTicks(10);
			server.runCommand("gamemode spectator @p");
			server.runCommand("execute at @p run tp @p ~0 ~4 ~-12 0 10");
			context.waitTicks(60);
			context.takeScreenshot("enchanted_forest_purple_grove_and_wildlife_glint_a");
			context.waitTicks(20);
			context.takeScreenshot("enchanted_forest_purple_grove_and_wildlife_glint_b");

			// 0.1.9's crafted planks. A wood set is exactly the kind of addition that ships
			// unseen: the block registers, the recipe resolves, the loot table drops, every
			// server-side check is green, and the only thing that can catch a wrong or missing
			// texture is a frame with the block in it. Both woods go in the same shot so the
			// two planks are readable against each other and against the logs they came from.
			server.runCommand("gamemode creative @p");
			server.runCommand("execute at @p run fill ~-3 ~ ~3 ~-2 ~1 ~4 enchanted_forest:enchanted_heartwood");
			server.runCommand("execute at @p run fill ~-1 ~ ~3 ~0 ~1 ~4 enchanted_forest:enchanted_heartwood_planks");
			server.runCommand("execute at @p run fill ~1 ~ ~3 ~2 ~1 ~4 enchanted_forest:enchanted_planks");
			server.runOnServer(minecraftServer -> auditPlanks(minecraftServer.overworld()));
			server.runCommand("gamemode spectator @p");
			server.runCommand("execute at @p run tp @p ~0 ~2 ~-2 0 15");
			context.waitTicks(40);
			context.takeScreenshot("enchanted_forest_heartwood_planks_and_planks");
		}
	}

	/** 0.1.9: the crafted heartwood planks exist as a real placed block, not just a registry entry. */
	private static void auditPlanks(net.minecraft.server.level.ServerLevel level) {
		List<String> problems = new ArrayList<>();
		BlockPos player = level.players().getFirst().blockPosition();
		int heartwoodPlanks = 0;
		int planks = 0;
		for (BlockPos candidate : BlockPos.betweenClosed(player.offset(-4, -1, 2), player.offset(4, 3, 5))) {
			if (level.getBlockState(candidate).is(EnchantedForestBlocks.ENCHANTED_HEARTWOOD_PLANKS)) heartwoodPlanks++;
			if (level.getBlockState(candidate).is(EnchantedForestBlocks.ENCHANTED_PLANKS)) planks++;
		}
		// each fill is 2 wide x 2 high x 2 deep
		if (heartwoodPlanks != 8) problems.add("heartwood planks placed " + heartwoodPlanks + "/8");
		if (planks != 8) problems.add("enchanted planks placed " + planks + "/8");
		if (level.recipeAccess().byKey(net.minecraft.resources.ResourceKey.create(
				Registries.RECIPE, EnchantedForest.id("enchanted_heartwood_planks"))).isEmpty()) {
			problems.add("heartwood planks recipe missing");
		}
		EnchantedForest.LOGGER.info("ENCHANTED_FOREST_PLANKS_AUDIT heartwood_planks={} planks={} problems={}",
				heartwoodPlanks, planks, problems.isEmpty() ? "none" : problems);
		if (!problems.isEmpty()) throw new IllegalStateException("Enchanted Forest planks audit failed: " + problems);
	}

	private static void auditRuntime(net.minecraft.server.level.ServerLevel level) {
		List<String> problems = new ArrayList<>();
		if (!DefaultAttributes.hasSupplier(EnchantedForestEntities.ENCHANTED_BIRD)) problems.add("bird attributes missing");
		if (!DefaultAttributes.hasSupplier(EnchantedForestEntities.ENCHANTED_FOX)) problems.add("fox attributes missing");
		if (!DefaultAttributes.hasSupplier(EnchantedForestEntities.ENCHANTED_BEAR)) problems.add("bear attributes missing");
		if (level.registryAccess().lookupOrThrow(Registries.BIOME)
				.get(EnchantedForestWorldgen.ENCHANTED_FOREST).isEmpty()) problems.add("biome missing");

		BlockPos player = level.players().getFirst().blockPosition();
		BlockPos base = null;
		int treeBases = 0;
		for (BlockPos candidate : BlockPos.betweenClosed(player.offset(-12, -2, -2), player.offset(12, 2, 18))) {
			if (level.getBlockState(candidate).is(EnchantedForestBlocks.ENCHANTED_HEARTWOOD)) {
				treeBases++;
				if (base == null) base = candidate.immutable();
			}
		}
		if (treeBases != 15) problems.add("dense grove placed " + treeBases + "/15 trees");
		if (base == null) {
			problems.add("placed tree has no heartwood base");
		} else {
			for (int y = 1; y < EnchantedTreeFeature.TRUNK_HEIGHT; y++) {
				if (!level.getBlockState(base.above(y)).is(EnchantedForestBlocks.ENCHANTED_LOG)
						&& !level.getBlockState(base.above(y)).is(EnchantedForestBlocks.ENCHANTED_AZURE_LOG)) {
					problems.add("trunk is broken at y=" + y);
				}
			}
			if (level.getBlockState(base.above(EnchantedTreeFeature.TRUNK_HEIGHT))
					.is(EnchantedForestBlocks.ENCHANTED_LOG)
					|| level.getBlockState(base.above(EnchantedTreeFeature.TRUNK_HEIGHT))
					.is(EnchantedForestBlocks.ENCHANTED_AZURE_LOG)) problems.add("trunk exceeds normal height");
			for (int x = -3; x <= 3; x++) for (int z = -3; z <= 3; z++) for (int y = EnchantedTreeFeature.TOTAL_HEIGHT; y <= 12; y++) {
				if (level.getBlockState(base.offset(x, y, z)).is(EnchantedForestBlocks.ENCHANTED_LEAVES)) {
					problems.add("canopy exceeds normal height");
				}
			}
		}

		long birds = level.getEntities(EnchantedForestEntities.ENCHANTED_BIRD, entity -> true).size();
		long foxes = level.getEntities(EnchantedForestEntities.ENCHANTED_FOX, entity -> true).size();
		long bears = level.getEntities(EnchantedForestEntities.ENCHANTED_BEAR, entity -> true).size();
		if (birds != 1) problems.add("bird summon count=" + birds);
		if (foxes != 1) problems.add("fox summon count=" + foxes);
		if (bears != 1) problems.add("bear summon count=" + bears);

		EnchantedForest.LOGGER.info(
				"ENCHANTED_FOREST_AUDIT tree={} grove={} trunk={} total={} bird={} fox={} bear={} problems={}",
				base, treeBases, EnchantedTreeFeature.TRUNK_HEIGHT, EnchantedTreeFeature.TOTAL_HEIGHT,
				birds, foxes, bears, problems.isEmpty() ? "none" : problems);
		if (!problems.isEmpty()) throw new IllegalStateException("Enchanted Forest runtime audit failed: " + problems);
	}
}
