package dev.lilkuzco.enchantedforest;

import dev.lilkuzco.enchantedforest.entity.EnchantedBear;
import dev.lilkuzco.enchantedforest.entity.EnchantedBird;
import dev.lilkuzco.enchantedforest.entity.EnchantedFox;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.level.levelgen.Heightmap;

public final class EnchantedForestEntities {
	public static final EntityType<EnchantedBird> ENCHANTED_BIRD = register("enchanted_bird",
			EntityType.Builder.of(EnchantedBird::new, MobCategory.CREATURE)
					.sized(0.5F, 0.9F).clientTrackingRange(8));
	public static final EntityType<EnchantedFox> ENCHANTED_FOX = register("enchanted_fox",
			EntityType.Builder.of(EnchantedFox::new, MobCategory.CREATURE)
					.sized(0.6F, 0.7F).clientTrackingRange(8));
	public static final EntityType<EnchantedBear> ENCHANTED_BEAR = register("enchanted_bear",
			EntityType.Builder.of(EnchantedBear::new, MobCategory.CREATURE)
					.sized(1.4F, 1.4F).clientTrackingRange(10));

	private EnchantedForestEntities() {
	}

	private static <T extends Entity> EntityType<T> register(String path, EntityType.Builder<T> builder) {
		Identifier id = EnchantedForest.id(path);
		ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
	}

	public static void register() {
		FabricDefaultAttributeRegistry.register(ENCHANTED_BIRD, Parrot.createAttributes());
		FabricDefaultAttributeRegistry.register(ENCHANTED_FOX, Fox.createAttributes());
		FabricDefaultAttributeRegistry.register(ENCHANTED_BEAR, PolarBear.createAttributes());

		SpawnPlacements.register(ENCHANTED_BIRD, SpawnPlacementTypes.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
		SpawnPlacements.register(ENCHANTED_FOX, SpawnPlacementTypes.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
		SpawnPlacements.register(ENCHANTED_BEAR, SpawnPlacementTypes.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
	}
}
