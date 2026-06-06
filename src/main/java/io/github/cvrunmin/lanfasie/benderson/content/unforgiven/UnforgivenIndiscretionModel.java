package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class UnforgivenIndiscretionModel extends HumanoidModel<UnforgivenIndiscretionRenderState> {

    public final ModelPart head;
    public final ModelPart upperHead;

	public UnforgivenIndiscretionModel(ModelPart root) {
		super(root);
        this.head = root.getChild("head");
        this.upperHead = this.head.getChild("upperHead");
	}

	public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, -14.0F);
        PartDefinition root = mesh.getRoot();
//        PartDefinition head = root.addOrReplaceChild(
//                "head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -6.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, -10.0F, 2.0F)
//        );
        PartDefinition head = root.addOrReplaceChild(
                "head", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.5F)), PartPose.offset(0.0F, -8.0F, 0.0F)
        );
        head.addOrReplaceChild(
                "hat", CubeListBuilder.create(), PartPose.ZERO
        );
        head.addOrReplaceChild(
                "upperHead", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -6.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0, -5, 2f)
        );
        root.addOrReplaceChild(
                "body", CubeListBuilder.create().texOffs(32, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F), PartPose.offset(0.0F, -8.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "right_arm", CubeListBuilder.create().texOffs(56, 0).addBox(-3.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(-3.0F, -6.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "left_arm", CubeListBuilder.create().texOffs(56, 0).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(5.0F, -6.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "right_leg", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(-2.0F, -2.0F, 0.0F)
        );
        root.addOrReplaceChild(
                "left_leg", CubeListBuilder.create().texOffs(56, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), PartPose.offset(2.0F, -2.0F, 0.0F)
        );
        return LayerDefinition.create(mesh, 64, 32);
	}

    @Override
    public void setupAnim(UnforgivenIndiscretionRenderState state) {
        super.setupAnim(state);
        this.head.visible = true;
        this.rightArm.xRot *= 0.5F;
        this.leftArm.xRot *= 0.5F;
        this.rightLeg.xRot *= 0.5F;
        this.leftLeg.xRot *= 0.5F;
        this.rightArm.xRot = Mth.clamp(this.rightArm.xRot, -0.4F, 0.4F);
        this.leftArm.xRot = Mth.clamp(this.leftArm.xRot, -0.4F, 0.4F);
        this.rightLeg.xRot = Mth.clamp(this.rightLeg.xRot, -0.4F, 0.4F);
        this.leftLeg.xRot = Mth.clamp(this.leftLeg.xRot, -0.4F, 0.4F);
//        this.head.y -= 5.0F;
//        this.hat.y += 5.0F;
    }
}