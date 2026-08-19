package dev.lilkuzco.enchantedforest.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.level.Level;

/** An enchanted-forest parrot with vanilla flight, taming, dancing, and mimicry. */
public final class EnchantedBird extends Parrot {
	public EnchantedBird(EntityType<? extends Parrot> entityType, Level level) {
		super(entityType, level);
	}
}
