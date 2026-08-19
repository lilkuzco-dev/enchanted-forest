package dev.lilkuzco.enchantedforest.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;

/** A luminous forest-floor plant that accepts any sturdy upward-facing surface. */
public class GlowingPlantBlock extends VegetationBlock {
	public static final MapCodec<GlowingPlantBlock> CODEC = simpleCodec(GlowingPlantBlock::new);

	public GlowingPlantBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends VegetationBlock> codec() {
		return CODEC;
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return state.isFaceSturdy(level, pos, Direction.UP);
	}
}
