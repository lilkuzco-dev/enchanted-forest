package dev.lilkuzco.enchantedforest.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.level.Level;

/** A rare enchanted bear with the polar bear's family-defense behavior. */
public final class EnchantedBear extends PolarBear {
	public EnchantedBear(EntityType<? extends PolarBear> entityType, Level level) {
		super(entityType, level);
	}
}
