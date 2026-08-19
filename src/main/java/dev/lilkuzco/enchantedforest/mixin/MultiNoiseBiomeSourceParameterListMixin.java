package dev.lilkuzco.enchantedforest.mixin;

import dev.lilkuzco.enchantedforest.worldgen.EnchantedForestWorldgen;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiNoiseBiomeSourceParameterList.class)
public class MultiNoiseBiomeSourceParameterListMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void enchantedForest$captureLookup(
			MultiNoiseBiomeSourceParameterList.Preset preset, HolderGetter<Biome> biomes,
			CallbackInfo ci) {
		EnchantedForestWorldgen.rememberBiomeLookup(biomes);
	}
}
