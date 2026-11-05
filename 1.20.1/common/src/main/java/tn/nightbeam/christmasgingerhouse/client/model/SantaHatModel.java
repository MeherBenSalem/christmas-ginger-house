package tn.nightbeam.christmasgingerhouse.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

/** A fur-trimmed cone that flops back over the wearer's head and ends in a pompom. */
public class SantaHatModel extends HumanoidModel<LivingEntity> {

    public SantaHatModel(ModelPart root) {
        super(root);
        setAllVisible(false);
        head.visible = true;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.5F, -9.0F, -4.5F, 9, 3, 9, new CubeDeformation(0.05F)), PartPose.ZERO);
        PartDefinition cone = head.addOrReplaceChild("cone", CubeListBuilder.create()
                .texOffs(0, 12).addBox(-3.5F, -3.0F, -3.5F, 7, 3, 7), PartPose.offsetAndRotation(0.0F, -8.8F, 0.3F, -0.18F, 0.0F, 0.0F));
        PartDefinition middle = cone.addOrReplaceChild("middle", CubeListBuilder.create()
                .texOffs(28, 12).addBox(-2.5F, -3.0F, -2.5F, 5, 3, 5), PartPose.offsetAndRotation(0.0F, -2.6F, 0.6F, -0.45F, 0.0F, 0.08F));
        PartDefinition tip = middle.addOrReplaceChild("tip", CubeListBuilder.create()
                .texOffs(48, 12).addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3), PartPose.offsetAndRotation(0.0F, -2.6F, 0.5F, -0.7F, 0.0F, 0.12F));
        tip.addOrReplaceChild("pompom", CubeListBuilder.create()
                .texOffs(0, 22).addBox(-1.5F, -3.0F, -1.5F, 3, 3, 3, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, -2.4F, 0.2F, -0.4F, 0.0F, 0.0F));
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 32);
    }
}
