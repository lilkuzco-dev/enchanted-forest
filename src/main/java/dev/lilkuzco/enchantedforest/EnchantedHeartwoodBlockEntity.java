package dev.lilkuzco.enchantedforest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class EnchantedHeartwoodBlockEntity extends BlockEntity {
	public EnchantedHeartwoodBlockEntity(BlockPos pos, BlockState state) {
		super(EnchantedForestBlockEntities.HEARTWOOD, pos, state);
	}
}
