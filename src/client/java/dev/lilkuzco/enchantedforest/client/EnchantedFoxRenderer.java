package dev.lilkuzco.enchantedforest.client;

import dev.lilkuzco.enchantedforest.EnchantedForest;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.resources.Identifier;

public final class EnchantedFoxRenderer extends FoxRenderer {
	private static final Identifier AWAKE = EnchantedForest.id("textures/entity/enchanted_fox.png");
	private static final Identifier BABY = EnchantedForest.id("textures/entity/enchanted_fox_baby.png");
	private static final Identifier SLEEPING = EnchantedForest.id("textures/entity/enchanted_fox_sleep.png");
	private static final Identifier SLEEPING_BABY = EnchantedForest.id("textures/entity/enchanted_fox_sleep_baby.png");

	public EnchantedFoxRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public Identifier getTextureLocation(FoxRenderState state) {
		if (state.isSleeping) return state.isBaby ? SLEEPING_BABY : SLEEPING;
		return state.isBaby ? BABY : AWAKE;
	}
}
