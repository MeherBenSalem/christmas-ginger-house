package tn.nightbeam.christmasgingerhouse.client.renderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import tn.nightbeam.christmasgingerhouse.Constants;
import tn.nightbeam.christmasgingerhouse.client.ModModelLayers;
import tn.nightbeam.christmasgingerhouse.client.model.KrampusModel;
import tn.nightbeam.christmasgingerhouse.entity.Krampus;

public class KrampusRenderer extends MobRenderer<Krampus, KrampusModel> {

    private static final ResourceLocation TEXTURE = Constants.id("textures/entity/krampus/krampus.png");
    private static final RenderType EYES = RenderType.eyes(Constants.id("textures/entity/krampus/krampus_eyes.png"));

    public KrampusRenderer(EntityRendererProvider.Context context) {
        super(context, new KrampusModel(context.bakeLayer(ModModelLayers.KRAMPUS)), 0.7F);
        addLayer(new Eyes(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Krampus entity) {
        return TEXTURE;
    }

    private static final class Eyes extends EyesLayer<Krampus, KrampusModel> {

        private Eyes(RenderLayerParent<Krampus, KrampusModel> parent) {
            super(parent);
        }

        @Override
        public RenderType renderType() {
            return EYES;
        }
    }
}
