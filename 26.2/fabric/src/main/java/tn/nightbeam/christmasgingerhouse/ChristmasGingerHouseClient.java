package tn.nightbeam.christmasgingerhouse;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import tn.nightbeam.christmasgingerhouse.client.FestiveArmorTextures;
import tn.nightbeam.christmasgingerhouse.client.ModModelLayers;
import tn.nightbeam.christmasgingerhouse.client.model.GingerbreadManModel;
import tn.nightbeam.christmasgingerhouse.client.model.KrampusModel;
import tn.nightbeam.christmasgingerhouse.client.model.ReindeerModel;
import tn.nightbeam.christmasgingerhouse.client.model.SantaHatModel;
import tn.nightbeam.christmasgingerhouse.client.renderer.GingerbreadManRenderer;
import tn.nightbeam.christmasgingerhouse.client.renderer.KrampusRenderer;
import tn.nightbeam.christmasgingerhouse.client.renderer.ReindeerRenderer;
import tn.nightbeam.christmasgingerhouse.registry.ModEntities;
import tn.nightbeam.christmasgingerhouse.registry.ModItems;

/** Block render layers are inferred from texture transparency in 26.x, so there is nothing to map. */
public class ChristmasGingerHouseClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelLayerRegistry.registerModelLayer(ModModelLayers.GINGERBREAD_MAN, GingerbreadManModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.REINDEER, ReindeerModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.KRAMPUS, KrampusModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ModModelLayers.SANTA_HAT, SantaHatModel::createLayer);

        EntityRendererRegistry.register(ModEntities.GINGERBREAD_MAN.get(), GingerbreadManRenderer::new);
        EntityRendererRegistry.register(ModEntities.REINDEER.get(), ReindeerRenderer::new);
        EntityRendererRegistry.register(ModEntities.KRAMPUS.get(), KrampusRenderer::new);

        // The Santa hat swaps the helmet layer for its own cone-and-pompom model; the sweater uses its equipment asset.
        ArmorRenderer.register(context -> {
            SantaHatModel hat = new SantaHatModel(context.bakeLayer(ModModelLayers.SANTA_HAT));
            return (poseStack, collector, stack, state, slot, light, contextModel) ->
                    ArmorRenderer.submitTransformCopyingModel(contextModel, state, hat, state, true, collector, poseStack,
                            RenderTypes.armorCutoutNoCull(FestiveArmorTextures.SANTA_HAT), light, OverlayTexture.NO_OVERLAY,
                            state.outlineColor, null);
        }, ModItems.SANTA_HAT.get());
    }
}
