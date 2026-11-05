package tn.nightbeam.christmasgingerhouse.client.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import tn.nightbeam.christmasgingerhouse.entity.GingerbreadMan;

/** A flat, two-pixel-thick cookie. Arms flap when it runs. */
public class GingerbreadManModel extends HierarchicalModel<GingerbreadMan> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public GingerbreadManModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();
        parts.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -6.0F, -1.0F, 6, 6, 2), PartPose.offset(0.0F, 14.0F, 0.0F));
        parts.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 8).addBox(-3.0F, 0.0F, -1.0F, 6, 5, 2), PartPose.offset(0.0F, 14.0F, 0.0F));
        parts.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(16, 0).addBox(-4.0F, -1.0F, -1.0F, 4, 3, 2), PartPose.offset(-3.0F, 15.0F, 0.0F));
        parts.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(16, 5).addBox(0.0F, -1.0F, -1.0F, 4, 3, 2), PartPose.offset(3.0F, 15.0F, 0.0F));
        parts.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 15).addBox(-1.5F, 0.0F, -1.0F, 3, 5, 2), PartPose.offset(-1.5F, 19.0F, 0.0F));
        parts.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(10, 15).addBox(-1.5F, 0.0F, -1.0F, 3, 5, 2), PartPose.offset(1.5F, 19.0F, 0.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(GingerbreadMan entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float stride = limbSwing * 0.9F;
        float swing = Math.min(limbSwingAmount, 1.0F);
        head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        head.xRot = headPitch * Mth.DEG_TO_RAD * 0.6F;
        head.zRot = Mth.sin(stride) * 0.12F * swing;
        body.zRot = Mth.sin(stride) * 0.06F * swing;

        rightLeg.xRot = Mth.cos(stride) * 1.3F * swing;
        leftLeg.xRot = Mth.cos(stride + Mth.PI) * 1.3F * swing;
        rightLeg.zRot = 0.1F;
        leftLeg.zRot = -0.1F;

        float idle = Mth.sin(ageInTicks * 0.09F) * 0.06F;
        float flap = Math.abs(Mth.sin(stride)) * 0.9F * swing;
        rightArm.zRot = -0.35F + idle - flap;
        leftArm.zRot = 0.35F - idle + flap;
        rightArm.xRot = Mth.cos(stride + Mth.PI) * 0.5F * swing;
        leftArm.xRot = Mth.cos(stride) * 0.5F * swing;
    }
}
