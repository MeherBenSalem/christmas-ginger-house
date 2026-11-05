package tn.nightbeam.christmasgingerhouse.client.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import tn.nightbeam.christmasgingerhouse.entity.Krampus;

/** Tall, shaggy and horned, with a bundle of birch switches in one hand and a chain in the other. */
public class KrampusModel extends HierarchicalModel<Krampus> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart tongue;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart chain;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart tail;

    public KrampusModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.tongue = head.getChild("tongue");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.chain = leftArm.getChild("chain");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();
        parts.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -3.0F, 10, 13, 6), PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition head = parts.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8),
                PartPose.offset(0.0F, -4.0F, -1.0F));
        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(32, 16).addBox(-2.0F, -3.0F, -6.0F, 4, 3, 2), PartPose.ZERO);
        head.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(44, 16).addBox(-1.0F, 0.0F, -0.5F, 2, 5, 1),
                PartPose.offsetAndRotation(0.0F, 0.0F, -5.0F, -0.15F, 0.0F, 0.0F));
        head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(50, 16).addBox(-3.0F, -1.0F, -0.5F, 3, 2, 1),
                PartPose.offsetAndRotation(-4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.35F));
        head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(50, 19).addBox(0.0F, -1.0F, -0.5F, 3, 2, 1),
                PartPose.offsetAndRotation(4.0F, -5.0F, 0.0F, 0.0F, 0.0F, -0.35F));
        PartDefinition rightHorn = head.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(0, 52).addBox(-1.0F, -4.0F, -1.0F, 2, 4, 2),
                PartPose.offsetAndRotation(-2.5F, -7.5F, 0.0F, -0.25F, 0.0F, -0.6F));
        rightHorn.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(8, 52).addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1),
                PartPose.offsetAndRotation(0.0F, -3.5F, 0.0F, -0.55F, 0.0F, 0.85F));
        PartDefinition leftHorn = head.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(12, 52).addBox(-1.0F, -4.0F, -1.0F, 2, 4, 2),
                PartPose.offsetAndRotation(2.5F, -7.5F, 0.0F, -0.25F, 0.0F, 0.6F));
        leftHorn.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(20, 52).addBox(-0.5F, -4.0F, -0.5F, 1, 4, 1),
                PartPose.offsetAndRotation(0.0F, -3.5F, 0.0F, -0.55F, 0.0F, -0.85F));

        PartDefinition rightArm = parts.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 32).addBox(-3.0F, -2.0F, -2.0F, 3, 16, 4),
                PartPose.offset(-5.0F, -2.0F, 0.0F));
        rightArm.addOrReplaceChild("switches", CubeListBuilder.create().texOffs(30, 52).addBox(-1.0F, 0.0F, -1.0F, 2, 9, 2),
                PartPose.offsetAndRotation(-1.5F, 12.5F, -0.5F, -1.1F, 0.0F, 0.0F));
        PartDefinition leftArm = parts.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(46, 32).addBox(0.0F, -2.0F, -2.0F, 3, 16, 4),
                PartPose.offset(5.0F, -2.0F, 0.0F));
        leftArm.addOrReplaceChild("chain", CubeListBuilder.create().texOffs(38, 52).addBox(-0.5F, 0.0F, -1.5F, 1, 9, 3),
                PartPose.offset(1.5F, 13.0F, 0.0F));

        parts.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4, 15, 4), PartPose.offset(-2.5F, 9.0F, 0.0F));
        parts.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 32).addBox(-2.0F, 0.0F, -2.0F, 4, 15, 4), PartPose.offset(2.5F, 9.0F, 0.0F));
        parts.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(46, 52).addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1),
                PartPose.offsetAndRotation(0.0F, 7.0F, 3.0F, 0.6F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(Krampus entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float stride = limbSwing * 0.6662F;
        head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        head.xRot = headPitch * Mth.DEG_TO_RAD;
        tongue.xRot = -0.15F + Mth.sin(ageInTicks * 0.15F) * 0.12F;

        rightLeg.xRot = Mth.cos(stride) * 1.1F * limbSwingAmount;
        leftLeg.xRot = Mth.cos(stride + Mth.PI) * 1.1F * limbSwingAmount;

        float sway = Mth.sin(ageInTicks * 0.067F) * 0.05F;
        rightArm.xRot = Mth.cos(stride + Mth.PI) * 0.9F * limbSwingAmount;
        leftArm.xRot = Mth.cos(stride) * 0.9F * limbSwingAmount;
        rightArm.zRot = 0.08F + sway;
        leftArm.zRot = -0.08F - sway;
        if (attackTime > 0.0F) {
            float lift = Mth.sin(attackTime * Mth.PI);
            rightArm.xRot = -2.2F * lift + 0.4F * (1.0F - lift);
            leftArm.xRot = -1.6F * lift;
        }
        chain.xRot = -leftArm.xRot * 0.6F + Mth.sin(ageInTicks * 0.1F) * 0.08F;
        tail.zRot = Mth.sin(ageInTicks * 0.1F) * 0.25F;
    }
}
