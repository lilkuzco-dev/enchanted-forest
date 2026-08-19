package dev.lilkuzco.enchantedforest.client;

import dev.lilkuzco.enchantedforest.EnchantedForest;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PolarBearRenderer;
import net.minecraft.client.renderer.entity.state.PolarBearRenderState;
import net.minecraft.resources.Identifier;

public final class EnchantedBearRenderer extends PolarBearRenderer {
	private static final Identifier ADULT = EnchantedForest.id("textures/entity/enchanted_bear.png");
	private static final Identifier BABY = EnchantedForest.id("textures/entity/enchanted_bear_baby.png");

	public EnchantedBearRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public Identifier getTextureLocation(PolarBearRenderState state) {
		return state.isBaby ? BABY : ADULT;
	}
}
