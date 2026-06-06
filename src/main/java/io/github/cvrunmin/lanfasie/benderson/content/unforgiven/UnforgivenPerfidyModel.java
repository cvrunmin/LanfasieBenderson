package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;


import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class UnforgivenPerfidyModel extends EntityModel<UnforgivenPerfidyRenderState> {
    protected final ModelPart leftHindLeg;
    protected final ModelPart rightHindLeg;
    protected final ModelPart leftFrontLeg;
    protected final ModelPart rightFrontLeg;
    protected final ModelPart tail1;
    protected final ModelPart tail2;
    protected final ModelPart head;
    protected final ModelPart body;
    protected final ModelPart rootBody;

	public UnforgivenPerfidyModel(ModelPart root) {
        super(root);
        this.rootBody = root.getChild("rootBody");
        this.head = rootBody.getChild("head");
        this.body = rootBody.getChild("body");
        this.tail1 = this.head.getChild("tail1");
        this.tail2 = this.tail1.getChild("tail2");
        this.leftHindLeg = rootBody.getChild("left_hind_leg");
        this.rightHindLeg = rootBody.getChild("right_hind_leg");
        this.leftFrontLeg = rootBody.getChild("left_front_leg");
        this.rightFrontLeg = rootBody.getChild("right_front_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("rootBody", CubeListBuilder.create(), PartPose.offset(0.0F, 17.0F, 1.0F));

        body.addOrReplaceChild("body", CubeListBuilder.create().texOffs(20, 0).addBox(-2.0F, -16.0F, -3.0F, 4.0F, 16.0F, 6.0F), PartPose.offset(0.0F, 2.0F, -1.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox("head", -2.5F, -2.0F, -3.0F, 5.0F, 4.0F, 5.0F)
		.texOffs(0, 24).addBox("nose", -1.5F, -0.0156F, -4.0F, 3.0F, 2.0F, 2.0F)
		.texOffs(0, 10).addBox("ear1", -2.0F, -3.0F, 0.0F, 1.0F, 1.0F, 2.0F)
		.texOffs(6, 10).addBox("ear2", 1.0F, -3.0F, 0.0F, 1.0F, 1.0F, 2.0F), PartPose.offset(0.0F, -16.0F, -1.0F));

		PartDefinition tail1 = head.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 15).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F), PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, (float) (Math.PI / 4), 0.0F, 0.0F));

        tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(4, 15).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, (float) (Math.PI / 4), 0.0F, 0.0F));

        body.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(8, 13).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 6.0F, 2.0F), PartPose.offset(1.1F, 1.0F, 6.0F));

        body.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(8, 13).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 6.0F, 2.0F), PartPose.offset(-1.1F, 1.0F, 6.0F));

        body.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 10.0F, 2.0F), PartPose.offset(1.2F, -3.0F, -5.0F));

        body.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, -0.2F, -1.0F, 2.0F, 10.0F, 2.0F), PartPose.offset(-1.2F, -3.0F, -5.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
	}

    @Override
    public void setupAnim(UnforgivenPerfidyRenderState state) {
        super.setupAnim(state);
        if (state.isSprinting) {
            this.tail2.y = this.tail1.y;
            this.tail2.z += 2.0F * state.ageScale;
            this.tail1.xRot = (float) (Math.PI / 2);
            this.tail2.xRot = (float) (Math.PI / 2);
        }
        this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
        this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
        float animationSpeed = state.walkAnimationSpeed;
        float animationPos = state.walkAnimationPos;
        if (state.isSprinting) {
            this.leftHindLeg.xRot = Mth.cos(animationPos * 0.6662F) * animationSpeed;
            this.rightHindLeg.xRot = Mth.cos(animationPos * 0.6662F + 0.3F) * animationSpeed;
            this.leftFrontLeg.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI + 0.3F) * animationSpeed;
            this.rightFrontLeg.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * animationSpeed;
            this.tail2.xRot = 1.7278761F + (float) (Math.PI / 10) * Mth.cos(animationPos) * animationSpeed;
        } else {
            this.leftHindLeg.xRot = Mth.cos(animationPos * 0.6662F) * animationSpeed;
            this.rightHindLeg.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * animationSpeed;
            this.leftFrontLeg.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * animationSpeed;
            this.rightFrontLeg.xRot = Mth.cos(animationPos * 0.6662F) * animationSpeed;
            this.tail2.xRot = 1.7278761F + 0.47123894F * Mth.cos(animationPos) * animationSpeed;
        }
    }

}