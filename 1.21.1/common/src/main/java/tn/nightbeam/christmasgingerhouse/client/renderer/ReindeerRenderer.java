package tn.nightbeam.christmasgingerhouse.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import tn.nightbeam.christmasgingerhouse.Constants;
import tn.nightbeam.christmasgingerhouse.client.ModModelLayers;
import tn.nightbeam.christmasgingerhouse.client.model.ReindeerModel;
import tn.nightbeam.christmasgingerhouse.entity.Reindeer;

public class ReindeerRenderer extends MobRenderer<Reindeer, ReindeerModel> {

    private static final ResourceLocation TEXTURE = Constants.id("textures/entity/reindeer/reindeer.png");
    private static final ResourceLocation RED_NOSE = Constants.id("textures/entity/reindeer/red_nose.png");

    public ReindeerRenderer(EntityRendererProvider.Context context) {
        super(context, new ReindeerModel(context.bakeLayer(ModModelLayers.REINDEER)), 0.6F);
        addLayer(new RedNoseLayer(this));
    }

    @Override
    protected void scale(Reindeer entity, PoseStack poseStack, float partialTick) {
        if (entity.isBaby()) {
            poseStack.scale(0.55F, 0.55F, 0.55F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Reindeer entity) {
        return TEXTURE;
    }

    /** Full-bright glow on the nose cube, only for red-nosed reindeer. */
    private static final class RedNoseLayer extends RenderLayer<Reindeer, ReindeerModel> {

        private RedNoseLayer(RenderLayerParent<Reindeer, ReindeerModel> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffers, int light, Reindeer entity, float limbSwing,
                           float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.hasRedNose() || entity.isInvisible()) {
                return;
            }
            VertexConsumer consumer = buffers.getBuffer(RenderType.eyes(RED_NOSE));
            float pulse = 0.85F + 0.15F * (float) Math.sin(ageInTicks * 0.15F);
            getParentModel().renderToBuffer(poseStack, consumer, 0xF00000, OverlayTexture.NO_OVERLAY,
                    FastColor.ARGB32.colorFromFloat(1.0F, pulse, pulse, pulse));
        }
    }
}
