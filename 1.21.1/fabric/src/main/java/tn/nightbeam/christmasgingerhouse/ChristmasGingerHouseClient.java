package tn.nightbeam.christmasgingerhouse;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import tn.nightbeam.christmasgingerhouse.client.FestiveArmorTextures;
import tn.nightbeam.christmasgingerhouse.client.ModModelLayers;
import tn.nightbeam.christmasgingerhouse.client.model.GingerbreadManModel;
import tn.nightbeam.christmasgingerhouse.client.model.KrampusModel;
import tn.nightbeam.christmasgingerhouse.client.model.ReindeerModel;
import tn.nightbeam.christmasgingerhouse.client.model.SantaHatModel;
import tn.nightbeam.christmasgingerhouse.client.renderer.GingerbreadManRenderer;
import tn.nightbeam.christmasgingerhouse.client.renderer.KrampusRenderer;
import tn.nightbeam.christmasgingerhouse.client.renderer.ReindeerRenderer;
import tn.nightbeam.christmasgingerhouse.registry.ModBlocks;
import tn.nightbeam.christmasgingerhouse.registry.ModEntities;
import tn.nightbeam.christmasgingerhouse.registry.ModItems;

public class ChristmasGingerHouseClient implements ClientModInitializer {

    private SantaHatModel hatModel;

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.GINGERBREAD_MAN, GingerbreadManModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.REINDEER, ReindeerModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.KRAMPUS, KrampusModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SANTA_HAT, SantaHatModel::createLayer);

        EntityRendererRegistry.register(ModEntities.GINGERBREAD_MAN.get(), GingerbreadManRenderer::new);
        EntityRendererRegistry.register(ModEntities.REINDEER.get(), ReindeerRenderer::new);
        EntityRendererRegistry.register(ModEntities.KRAMPUS.get(), KrampusRenderer::new);

        Block[] cutout = {
                ModBlocks.CHRISTMAS_LIGHTS.get(), ModBlocks.WREATH.get(), ModBlocks.STOCKING.get(), ModBlocks.STAR_TOPPER.get(),
                ModBlocks.RED_ORNAMENT.get(), ModBlocks.GOLD_ORNAMENT.get(), ModBlocks.BLUE_ORNAMENT.get(),
                ModBlocks.GINGER_CROP.get(), ModBlocks.WILD_GINGER.get(), ModBlocks.GINGERBREAD_DOOR.get(), ModBlocks.GINGERBREAD_TRAPDOOR.get(),
                ModBlocks.RED_PRESENT.get(), ModBlocks.GREEN_PRESENT.get(), ModBlocks.BLUE_PRESENT.get(), ModBlocks.GOLD_PRESENT.get()};
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), cutout);

        ArmorRenderer.register((poseStack, buffers, stack, entity, slot, light, contextModel) -> {
            if (hatModel == null) {
                hatModel = new SantaHatModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayers.SANTA_HAT));
            }
            contextModel.copyPropertiesTo(hatModel);
            hatModel.setAllVisible(false);
            hatModel.head.visible = true;
            ArmorRenderer.renderPart(poseStack, buffers, light, stack, hatModel, FestiveArmorTextures.SANTA_HAT);
        }, ModItems.SANTA_HAT.get());
        // The sweater renders through the vanilla armor layer using its material texture.
    }
}
