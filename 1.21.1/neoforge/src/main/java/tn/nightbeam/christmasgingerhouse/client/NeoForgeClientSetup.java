package tn.nightbeam.christmasgingerhouse.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import tn.nightbeam.christmasgingerhouse.client.model.GingerbreadManModel;
import tn.nightbeam.christmasgingerhouse.client.model.KrampusModel;
import tn.nightbeam.christmasgingerhouse.client.model.ReindeerModel;
import tn.nightbeam.christmasgingerhouse.client.model.SantaHatModel;
import tn.nightbeam.christmasgingerhouse.client.renderer.GingerbreadManRenderer;
import tn.nightbeam.christmasgingerhouse.client.renderer.KrampusRenderer;
import tn.nightbeam.christmasgingerhouse.client.renderer.ReindeerRenderer;
import tn.nightbeam.christmasgingerhouse.registry.ModEntities;
import tn.nightbeam.christmasgingerhouse.registry.ModItems;

/** Client-only wiring. Block render types come from {@code render_type} in the block model JSON. */
public final class NeoForgeClientSetup {

    private NeoForgeClientSetup() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(NeoForgeClientSetup::onLayers);
        modBus.addListener(NeoForgeClientSetup::onRenderers);
        modBus.addListener(NeoForgeClientSetup::onClientExtensions);
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

    private static void onClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private SantaHatModel model;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                if (model == null) {
                    model = new SantaHatModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayers.SANTA_HAT));
                }
                return model;
            }
        }, ModItems.SANTA_HAT.get());
    }
}
