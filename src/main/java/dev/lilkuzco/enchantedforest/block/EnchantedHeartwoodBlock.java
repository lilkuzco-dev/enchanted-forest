package dev.lilkuzco.enchantedforest.block;

import dev.lilkuzco.enchantedforest.EnchantedHeartwoodBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** One render anchor at the foot of each tree; the rest of the tree remains ordinary blocks. */
public final class EnchantedHeartwoodBlock extends RotatedPillarBlock implements EntityBlock {
	public EnchantedHeartwoodBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new EnchantedHeartwoodBlockEntity(pos, state);
	}
}
