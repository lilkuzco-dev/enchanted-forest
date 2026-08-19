package dev.lilkuzco.enchantedforest.mixin;

import dev.lilkuzco.enchantedforest.worldgen.EnchantedForestWorldgen;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {
	@Unique
	private Climate.ParameterList<Holder<Biome>> enchantedForest$claimed;

	@Inject(method = "parameters", at = @At("RETURN"), cancellable = true)
	private void enchantedForest$claimSlice(
			CallbackInfoReturnable<Climate.ParameterList<Holder<Biome>>> cir) {
		if (this.enchantedForest$claimed == null) {
			this.enchantedForest$claimed = EnchantedForestWorldgen.claimIn(cir.getReturnValue());
		}
		cir.setReturnValue(this.enchantedForest$claimed);
	}
}
