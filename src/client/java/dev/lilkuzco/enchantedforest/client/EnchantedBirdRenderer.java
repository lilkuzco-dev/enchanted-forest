package dev.lilkuzco.enchantedforest.client;

import dev.lilkuzco.enchantedforest.EnchantedForest;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.resources.Identifier;

public final class EnchantedBirdRenderer extends ParrotRenderer {
	private static final Identifier RED_BLUE = EnchantedForest.id("textures/entity/enchanted_bird_red_blue.png");
	private static final Identifier BLUE = EnchantedForest.id("textures/entity/enchanted_bird_blue.png");
	private static final Identifier GREEN = EnchantedForest.id("textures/entity/enchanted_bird_green.png");
	private static final Identifier YELLOW_BLUE = EnchantedForest.id("textures/entity/enchanted_bird_yellow_blue.png");
	private static final Identifier GRAY = EnchantedForest.id("textures/entity/enchanted_bird_grey.png");

	public EnchantedBirdRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public Identifier getTextureLocation(ParrotRenderState state) {
		return switch (state.variant) {
			case RED_BLUE -> RED_BLUE;
			case BLUE -> BLUE;
			case GREEN -> GREEN;
			case YELLOW_BLUE -> YELLOW_BLUE;
			case GRAY -> GRAY;
		};
	}
}
