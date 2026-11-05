package tn.nightbeam.christmasgingerhouse.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import javax.annotation.Nonnull;
import tn.nightbeam.christmasgingerhouse.client.FestiveArmorTextures;
import tn.nightbeam.christmasgingerhouse.client.ModModelLayers;
import tn.nightbeam.christmasgingerhouse.client.model.SantaHatModel;

import java.util.function.Consumer;

/** Forge hook for the Santa hat's custom model and both festive textures. */
public class ForgeFestiveArmorItem extends ArmorItem {

    public ForgeFestiveArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return (getType() == Type.HELMET ? FestiveArmorTextures.SANTA_HAT : FestiveArmorTextures.SWEATER).toString();
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        if (getType() != Type.HELMET) {
            return;
        }
        consumer.accept(new IClientItemExtensions() {
            private SantaHatModel model;

            @Override
            public @Nonnull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                if (model == null) {
                    model = new SantaHatModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayers.SANTA_HAT));
                }
                return model;
            }
        });
    }
}
