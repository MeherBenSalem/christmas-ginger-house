package tn.nightbeam.christmasgingerhouse.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import tn.nightbeam.christmasgingerhouse.Constants;
import tn.nightbeam.christmasgingerhouse.client.ModModelLayers;
import tn.nightbeam.christmasgingerhouse.client.model.ReindeerModel;
import tn.nightbeam.christmasgingerhouse.client.state.ReindeerRenderState;
import tn.nightbeam.christmasgingerhouse.entity.Reindeer;

public class ReindeerRenderer extends MobRenderer<Reindeer, ReindeerRenderState, ReindeerModel> {

    private static final Identifier TEXTURE = Constants.id("textures/entity/reindeer/reindeer.png");
    private static final RenderType RED_NOSE = RenderTypes.eyes(Constants.id("textures/entity/reindeer/red_nose.png"));

    public ReindeerRenderer(EntityRendererProvider.Context context) {
        super(context, new ReindeerModel(context.bakeLayer(ModModelLayers.REINDEER)), 0.6F);
        addLayer(new RedNoseLayer(this));
    }

    @Override
    public ReindeerRenderState createRenderState() {
        return new ReindeerRenderState();
    }

    @Override
    public void extractRenderState(Reindeer entity, ReindeerRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.redNose = entity.hasRedNose();
    }

    @Override
    protected void scale(ReindeerRenderState state, PoseStack poseStack) {
        if (state.isBaby) {
            poseStack.scale(0.55F, 0.55F, 0.55F);
        }
    }

    @Override
    public Identifier getTextureLocation(ReindeerRenderState state) {
        return TEXTURE;
    }

    /** Full-bright glow on the nose cube, only for red-nosed reindeer. */
    private static final class RedNoseLayer extends EyesLayer<ReindeerRenderState, ReindeerModel> {

        private RedNoseLayer(RenderLayerParent<ReindeerRenderState, ReindeerModel> parent) {
            super(parent);
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, ReindeerRenderState state, float yRot, float xRot) {
            if (state.redNose && !state.isInvisible) {
                super.submit(poseStack, collector, light, state, yRot, xRot);
            }
        }

        @Override
        public RenderType renderType() {
            return RED_NOSE;
        }
    }
}
