package tn.nightbeam.christmasgingerhouse.client;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import tn.nightbeam.christmasgingerhouse.client.model.GingerbreadManModel;
import tn.nightbeam.christmasgingerhouse.client.model.KrampusModel;
import tn.nightbeam.christmasgingerhouse.client.model.ReindeerModel;
import tn.nightbeam.christmasgingerhouse.client.model.SantaHatModel;
import tn.nightbeam.christmasgingerhouse.client.renderer.GingerbreadManRenderer;
import tn.nightbeam.christmasgingerhouse.client.renderer.KrampusRenderer;
import tn.nightbeam.christmasgingerhouse.client.renderer.ReindeerRenderer;
import tn.nightbeam.christmasgingerhouse.registry.ModEntities;

/** Client-only wiring. Block render types come from {@code render_type} in the block model JSON. */
public final class ForgeClientSetup {

    private ForgeClientSetup() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(ForgeClientSetup::onLayers);
        modBus.addListener(ForgeClientSetup::onRenderers);
    }

    private static void onLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.GINGERBREAD_MAN, GingerbreadManModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.REINDEER, ReindeerModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.KRAMPUS, KrampusModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SANTA_HAT, SantaHatModel::createLayer);
    }

    private static void onRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.GINGERBREAD_MAN.get(), GingerbreadManRenderer::new);
        event.registerEntityRenderer(ModEntities.REINDEER.get(), ReindeerRenderer::new);
        event.registerEntityRenderer(ModEntities.KRAMPUS.get(), KrampusRenderer::new);
    }
}
