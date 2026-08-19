package dev.lilkuzco.enchantedforest.client;

import dev.lilkuzco.enchantedforest.EnchantedForestBlockEntities;
import dev.lilkuzco.enchantedforest.EnchantedForestEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public final class EnchantedForestClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(
				EnchantedForestBlockEntities.HEARTWOOD, EnchantedTreeRenderer::new);
		EntityRendererRegistry.register(EnchantedForestEntities.ENCHANTED_BIRD, EnchantedBirdRenderer::new);
		EntityRendererRegistry.register(EnchantedForestEntities.ENCHANTED_FOX, EnchantedFoxRenderer::new);
		EntityRendererRegistry.register(EnchantedForestEntities.ENCHANTED_BEAR, EnchantedBearRenderer::new);
	}
}
