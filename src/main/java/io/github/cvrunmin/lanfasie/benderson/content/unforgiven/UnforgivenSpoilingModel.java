package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.RabbitAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class UnforgivenSpoilingModel extends EntityModel<UnforgivenSpoilingRenderState> {
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart head;
	private final ModelPart leftEar;
	private final ModelPart rightEar;
	private final ModelPart frontlegs;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart backlegs;
	private final ModelPart rightBackLeg;
	private final ModelPart leftBackLeg;
	private final KeyframeAnimation hopAnimation;

	public UnforgivenSpoilingModel(ModelPart root) {
        super(root);
		this.body = root.getChild("body");
		this.tail = this.body.getChild("tail");
		this.head = this.body.getChild("head");
		this.leftEar = this.head.getChild("left_ear");
		this.rightEar = this.head.getChild("right_ear");
		this.frontlegs = this.body.getChild("frontlegs");
		this.rightFrontLeg = this.frontlegs.getChild("right_front_leg");
		this.leftFrontLeg = this.frontlegs.getChild("left_front_leg");
		this.backlegs = root.getChild("backlegs");
		this.rightBackLeg = this.backlegs.getChild("right_hind_leg");
		this.leftBackLeg = this.backlegs.getChild("left_hind_leg");
		this.hopAnimation = RabbitAnimation.HOP.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -13.0F, 10.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 22.5F, 5.0F, (float) (-Math.PI * 50 / 180), 0.0F, 0.0F));

        body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(36, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, (float) (Math.PI * 40 / 180), 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 22).addBox(-2.5F, -6.5F, -5.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, -11.0F, (float) -(Math.PI * 50 / 180), 0.0F, 0.0F));

		PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.offset(1.5F, -5.0F, -1.0F));

        left_ear.addOrReplaceChild("left_ear_r1", CubeListBuilder.create().texOffs(34, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, (float) (-Math.PI * 80 / 180), (float) (Math.PI * 30 / 180), 0.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.offset(-1.5F, -6.0F, -1.0F));

        right_ear.addOrReplaceChild("right_ear_r1", CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, (float) (-Math.PI * 80 / 180), (float) (-Math.PI * 30 / 180), 0.0F));

        PartDefinition frontlegs = body.addOrReplaceChild("frontlegs", CubeListBuilder.create(), PartPose.offset(0.0F, -1.5349F, -11.3107F));

        frontlegs.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(20, 22).addBox(-1.9F, -1.0F, -0.9F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 1.9239F, 0.3827F, 0.3927F, 0.0F, 0.0F));

        frontlegs.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(28, 22).addBox(0.0F, -1.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 1.9239F, 0.4827F, 0.3927F, 0.0F, 0.0F));

        PartDefinition backlegs = partdefinition.addOrReplaceChild("backlegs", CubeListBuilder.create(), PartPose.offset(0.0F, 23.0F, 5.5F));

		PartDefinition rightBackLeg = backlegs.addOrReplaceChild("right_hind_leg", CubeListBuilder.create(), PartPose.offset(-4.0F, 0.0F, -1.5F));

        rightBackLeg.addOrReplaceChild("right_haunch", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, -1.0F, -6.0F, 4.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, (float) (Math.PI * 22.5 / 180), 0.0F));

        PartDefinition leftBackLeg = backlegs.addOrReplaceChild("left_hind_leg", CubeListBuilder.create(), PartPose.offset(4.0F, 0.0F, -1.5F));

        leftBackLeg.addOrReplaceChild("left_haunch", CubeListBuilder.create().texOffs(24, 32).addBox(-2.0F, -1.0F, -6.0F, 4.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, (float) -(Math.PI * 22.5 / 180), 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
	}

    @Override
    public void setupAnim(UnforgivenSpoilingRenderState state) {
        super.setupAnim(state);
        this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
        this.head.xRot = state.xRot * (float) (Math.PI / 180.0) + (float) (Math.PI * 50 / 180);
		this.hopAnimation.apply(state.hopAnimationState, state.ageInTicks);
    }
}