package tn.nightbeam.christmasgingerhouse.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import tn.nightbeam.christmasgingerhouse.Constants;
import tn.nightbeam.christmasgingerhouse.client.ModModelLayers;
import tn.nightbeam.christmasgingerhouse.client.model.KrampusModel;
import tn.nightbeam.christmasgingerhouse.client.state.KrampusRenderState;
import tn.nightbeam.christmasgingerhouse.entity.Krampus;

public class KrampusRenderer extends MobRenderer<Krampus, KrampusRenderState, KrampusModel> {

    private static final Identifier TEXTURE = Constants.id("textures/entity/krampus/krampus.png");
    private static final RenderType EYES = RenderTypes.eyes(Constants.id("textures/entity/krampus/krampus_eyes.png"));

    public KrampusRenderer(EntityRendererProvider.Context context) {
        super(context, new KrampusModel(context.bakeLayer(ModModelLayers.KRAMPUS)), 0.7F);
        addLayer(new Eyes(this));
    }

    @Override
    public KrampusRenderState createRenderState() {
        return new KrampusRenderState();
    }

    @Override
    public void extractRenderState(Krampus entity, KrampusRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.attackTime = entity.getAttackAnim(partialTicks);
    }

    @Override
    public Identifier getTextureLocation(KrampusRenderState state) {
        return TEXTURE;
    }

    private static final class Eyes extends EyesLayer<KrampusRenderState, KrampusModel> {

        private Eyes(RenderLayerParent<KrampusRenderState, KrampusModel> parent) {
            super(parent);
        }

        @Override
        public RenderType renderType() {
            return EYES;
        }
    }
}
