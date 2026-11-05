package tn.nightbeam.christmasgingerhouse.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import tn.nightbeam.christmasgingerhouse.Constants;
import tn.nightbeam.christmasgingerhouse.client.ModModelLayers;
import tn.nightbeam.christmasgingerhouse.client.model.GingerbreadManModel;
import tn.nightbeam.christmasgingerhouse.entity.GingerbreadMan;

public class GingerbreadManRenderer extends MobRenderer<GingerbreadMan, GingerbreadManModel> {

    private static final ResourceLocation TEXTURE = Constants.id("textures/entity/gingerbread_man.png");

    public GingerbreadManRenderer(EntityRendererProvider.Context context) {
        super(context, new GingerbreadManModel(context.bakeLayer(ModModelLayers.GINGERBREAD_MAN)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(GingerbreadMan entity) {
        return TEXTURE;
    }
}
