package dev.lilkuzco.enchantedforest.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.level.Level;

/** An enchanted-forest fox retaining vanilla fox hunting, sleeping, and trust behavior. */
public final class EnchantedFox extends Fox {
	public EnchantedFox(EntityType<? extends Fox> entityType, Level level) {
		super(entityType, level);
	}
}
