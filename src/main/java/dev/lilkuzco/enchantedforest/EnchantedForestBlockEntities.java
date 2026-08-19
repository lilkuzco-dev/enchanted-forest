package dev.lilkuzco.enchantedforest;

import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class EnchantedForestBlockEntities {
	public static final BlockEntityType<EnchantedHeartwoodBlockEntity> HEARTWOOD =
			new BlockEntityType<>(EnchantedHeartwoodBlockEntity::new,
					Set.of(EnchantedForestBlocks.ENCHANTED_HEARTWOOD));

	public static void register() {
		Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
				EnchantedForest.id("enchanted_heartwood"), HEARTWOOD);
	}

	private EnchantedForestBlockEntities() {
	}
}
