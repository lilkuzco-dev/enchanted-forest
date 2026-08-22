package dev.lilkuzco.enchantedforest.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.lilkuzco.enchantedforest.worldgen.EnchantedForestWorldgen;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Claims birch-forest entries from whatever parameter list this world actually uses.
 *
 * <h2>Why {@code @ModifyReturnValue} and not a cancellable {@code @Inject}</h2>
 * Because this is not the only empire mod that rewrites this return value. Waldschatten
 * claims its own slice of the same list, and until 0.1.11 both mods did it with
 * {@code @Inject(at = RETURN, cancellable = true)} + {@code setReturnValue}. Mixin emits
 * {@code if (cancelled) return} after each callback at an injection point, so the first
 * handler to cancel ends the method and every later handler is skipped — silently: the
 * skipped mod's code is never entered, so it cannot even log that it lost. Measured
 * 2026-08-22: Enchanted Forest's config registers first, so it claimed 176 entries and
 * Waldschatten's biome was absent from every world on every client and the server.
 *
 * <p>{@code @ModifyReturnValue} modifiers chain instead — each receives the previous
 * one's output — so both claims land whichever mod applies first. The two claims touch
 * disjoint biomes (birch forests here, forest/dark forest there), so order does not
 * change the result either.
 *
 * <p>Memoised because this is called for every biome cell during chunk generation. The
 * underlying field is final, so the answer cannot change.
 */
@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {
	@Unique
	private Climate.ParameterList<Holder<Biome>> enchantedForest$claimed;

	@ModifyReturnValue(method = "parameters", at = @At("RETURN"))
	private Climate.ParameterList<Holder<Biome>> enchantedForest$claimSlice(
			Climate.ParameterList<Holder<Biome>> original) {
		if (this.enchantedForest$claimed == null) {
			this.enchantedForest$claimed = EnchantedForestWorldgen.claimIn(original);
		}
		return this.enchantedForest$claimed;
	}
}
