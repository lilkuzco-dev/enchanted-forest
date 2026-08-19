package dev.lilkuzco.enchantedforest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.lilkuzco.enchantedforest.EnchantedHeartwoodBlockEntity;
import dev.lilkuzco.enchantedforest.worldgen.EnchantedTreeFeature;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** Draws the real animated equipment-glint pipeline over one continuous tree trunk. */
public final class EnchantedTreeRenderer
		implements BlockEntityRenderer<EnchantedHeartwoodBlockEntity, EnchantedTreeRenderState> {
	public EnchantedTreeRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public EnchantedTreeRenderState createRenderState() {
		return new EnchantedTreeRenderState();
	}

	@Override
	public void extractRenderState(EnchantedHeartwoodBlockEntity blockEntity,
			EnchantedTreeRenderState state, float tickProgress, Vec3 cameraPos,
			ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(
				blockEntity, state, tickProgress, cameraPos, crumblingOverlay);
	}

	@Override
	public void submit(EnchantedTreeRenderState state, PoseStack poseStack,
			SubmitNodeCollector queue, CameraRenderState cameraState) {
		queue.order(0).submitCustomGeometry(poseStack, RenderTypes.glint(),
				(pose, buffer) -> renderTrunkGlint(pose, buffer));
	}

	private static void renderTrunkGlint(PoseStack.Pose pose, VertexConsumer buffer) {
		float height = EnchantedTreeFeature.TRUNK_HEIGHT;
		quad(pose, buffer, 0, 0, 0, 1, 0, 0, 1, height, 0, 0, height, 0);
		quad(pose, buffer, 1, 0, 1, 0, 0, 1, 0, height, 1, 1, height, 1);
		quad(pose, buffer, 0, 0, 1, 0, 0, 0, 0, height, 0, 0, height, 1);
		quad(pose, buffer, 1, 0, 0, 1, 0, 1, 1, height, 1, 1, height, 0);
	}

	private static void quad(PoseStack.Pose pose, VertexConsumer buffer,
			float x0, float y0, float z0, float x1, float y1, float z1,
			float x2, float y2, float z2, float x3, float y3, float z3) {
		vertex(pose, buffer, x0, y0, z0, 0, 0);
		vertex(pose, buffer, x1, y1, z1, 1, 0);
		vertex(pose, buffer, x2, y2, z2, 1, 1);
		vertex(pose, buffer, x3, y3, z3, 0, 1);
	}

	private static void vertex(PoseStack.Pose pose, VertexConsumer buffer,
			float x, float y, float z, float u, float v) {
		buffer.addVertex(pose, x, y, z).setColor(0xFFFFFFFF).setUv(u, v);
	}

	@Override
	public int getViewDistance() {
		return 64;
	}
}
