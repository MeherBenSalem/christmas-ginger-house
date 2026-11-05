package tn.nightbeam.christmasgingerhouse.client.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import tn.nightbeam.christmasgingerhouse.entity.Reindeer;

/** Quadruped with a raised neck, branching antlers and a stub tail. The nose is its own cube so it can glow. */
public class ReindeerModel extends HierarchicalModel<Reindeer> {

    private static final float NECK_TILT = 0.45F;

    private final ModelPart root;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart tail;
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private final ModelPart backRightLeg;
    private final ModelPart backLeftLeg;

    public ReindeerModel(ModelPart root) {
        this.root = root;
        this.neck = root.getChild("neck");
        this.head = neck.getChild("head");
        this.rightEar = head.getChild("right_ear");
        this.leftEar = head.getChild("left_ear");
        this.tail = root.getChild("tail");
        this.frontRightLeg = root.getChild("front_right_leg");
        this.frontLeftLeg = root.getChild("front_left_leg");
        this.backRightLeg = root.getChild("back_right_leg");
        this.backLeftLeg = root.getChild("back_left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();
        parts.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8, 8, 16), PartPose.offset(0.0F, 11.0F, 0.0F));

        PartDefinition neck = parts.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, -7.0F, -2.0F, 4, 8, 4),
                PartPose.offsetAndRotation(0.0F, 8.0F, -6.0F, NECK_TILT, 0.0F, 0.0F));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 24).addBox(-2.5F, -3.0F, -4.0F, 5, 5, 6),
                PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, -NECK_TILT, 0.0F, 0.0F));
        head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(22, 24).addBox(-1.5F, -0.5F, -7.0F, 3, 3, 3), PartPose.ZERO);
        head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(34, 24).addBox(-1.0F, -0.5F, -8.0F, 2, 2, 1), PartPose.ZERO);
        head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(40, 24).addBox(-3.0F, -0.5F, -1.0F, 3, 1, 2),
                PartPose.offsetAndRotation(-2.5F, -2.0F, 0.5F, 0.0F, 0.0F, -0.45F));
        head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(40, 27).addBox(0.0F, -0.5F, -1.0F, 3, 1, 2),
                PartPose.offsetAndRotation(2.5F, -2.0F, 0.5F, 0.0F, 0.0F, 0.45F));
        antler(head, "right_antler", -1.5F, -1, 50, 24);
        antler(head, "left_antler", 1.5F, 1, 50, 32);

        parts.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(48, 40).addBox(-1.5F, 0.0F, 0.0F, 3, 3, 2),
                PartPose.offsetAndRotation(0.0F, 8.0F, 8.0F, 0.6F, 0.0F, 0.0F));
        parts.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(0, 36).addBox(-1.5F, 0.0F, -1.5F, 3, 9, 3), PartPose.offset(-2.5F, 15.0F, -6.0F));
        parts.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(12, 36).addBox(-1.5F, 0.0F, -1.5F, 3, 9, 3), PartPose.offset(2.5F, 15.0F, -6.0F));
        parts.addOrReplaceChild("back_right_leg", CubeListBuilder.create().texOffs(24, 36).addBox(-1.5F, 0.0F, -1.5F, 3, 9, 3), PartPose.offset(-2.5F, 15.0F, 6.0F));
        parts.addOrReplaceChild("back_left_leg", CubeListBuilder.create().texOffs(36, 36).addBox(-1.5F, 0.0F, -1.5F, 3, 9, 3), PartPose.offset(2.5F, 15.0F, 6.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    /** A swept-back beam with a brow tine, a side tine and a forked tip. {@code side} is -1 (right) or 1 (left). */
    private static void antler(PartDefinition head, String name, float x, int side, int u, int v) {
        PartDefinition beam = head.addOrReplaceChild(name, CubeListBuilder.create().texOffs(u, v).addBox(-0.5F, -7.0F, -0.5F, 1, 7, 1),
                PartPose.offsetAndRotation(x, -2.5F, 0.0F, -0.3F, 0.0F, side * 0.4F));
        beam.addOrReplaceChild("brow_tine", CubeListBuilder.create().texOffs(u + 4, v).addBox(-0.5F, -3.0F, -0.5F, 1, 3, 1),
                PartPose.offsetAndRotation(0.0F, -2.5F, 0.0F, -0.95F, 0.0F, 0.0F));
        float sideX = side < 0 ? -3.0F : 0.0F;
        beam.addOrReplaceChild("side_tine", CubeListBuilder.create().texOffs(u + 4, v + 4).addBox(sideX, -0.5F, -0.5F, 3, 1, 1),
                PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.0F, 0.0F, side * -0.6F));
        beam.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(u + 8, v).addBox(-0.5F, -2.0F, -0.5F, 1, 2, 1),
                PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, -0.5F, 0.0F, side * 0.35F));
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(Reindeer entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float stride = limbSwing * 0.6662F;
        neck.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.6F;
        neck.xRot = NECK_TILT + Mth.cos(stride * 2.0F) * 0.05F * limbSwingAmount;
        head.xRot = -NECK_TILT + headPitch * Mth.DEG_TO_RAD * 0.6F;

        frontRightLeg.xRot = Mth.cos(stride) * 1.3F * limbSwingAmount;
        backLeftLeg.xRot = Mth.cos(stride) * 1.3F * limbSwingAmount;
        frontLeftLeg.xRot = Mth.cos(stride + Mth.PI) * 1.3F * limbSwingAmount;
        backRightLeg.xRot = Mth.cos(stride + Mth.PI) * 1.3F * limbSwingAmount;

        float flick = Mth.sin(ageInTicks * 0.12F);
        flick = flick > 0.92F ? (flick - 0.92F) * 6.0F : 0.0F;
        rightEar.zRot = -0.45F - flick;
        leftEar.zRot = 0.45F + flick;
        tail.zRot = Mth.sin(ageInTicks * 0.2F) * 0.15F;
    }
}
