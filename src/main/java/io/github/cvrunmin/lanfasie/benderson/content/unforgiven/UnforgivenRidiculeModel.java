package io.github.cvrunmin.lanfasie.benderson.content.unforgiven;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class UnforgivenRidiculeModel extends EntityModel<UnforgivenRidiculeRenderState> {
	private final ModelPart head;
	private final ModelPart stem;
	private final ModelPart branchAnimator;
	private final ModelPart branch;
	private final ModelPart[] subBranches;

	public UnforgivenRidiculeModel(ModelPart root) {
        super(root);
		this.head = root.getChild("head");
		this.stem = root.getChild("stem");
		this.branchAnimator = this.stem.getChild("branchAnimator");
		this.branch = this.branchAnimator.getChild("branch");
        this.subBranches = new ModelPart[3];
        this.subBranches[0] = this.branch.getChild("subbranch1");
        this.subBranches[1] = this.branch.getChild("subbranch2");
        this.subBranches[2] = this.branch.getChild("subbranch3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, 11.0F, 0.0F));

        PartDefinition Stem = partdefinition.addOrReplaceChild("stem", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F), PartPose.offset(0.0F, 11.0F, 0.0F));

		PartDefinition BranchAnimator = Stem.addOrReplaceChild("branchAnimator", CubeListBuilder.create(), PartPose.offset(0.0F, -9.0F, 0.0F));

		PartDefinition Branch = BranchAnimator.addOrReplaceChild("branch", CubeListBuilder.create().texOffs(8, 16).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F), PartPose.offset(0.0F, 0.0F, 0.0F));

        Branch.addOrReplaceChild("subbranch1", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 8.0F, 2.0F)
                .texOffs(0, 26).addBox(-2.5F, -13.0F, -2.5F, 5.0F, 5.0F, 5.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, (float) -(Math.PI * 70.5 / 180f), 0.0F, 0.0F));

        Branch.addOrReplaceChild("subbranch2", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 8.0F, 2.0F)
                .texOffs(0, 26).addBox(-2.5F, -13.0F, -2.5F, 5.0F, 5.0F, 5.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, (float) -(Math.PI * 70.5 / 180f), (float) -(Math.PI * 120 / 180f), 0.0F));

        Branch.addOrReplaceChild("subbranch3", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 8.0F, 2.0F)
                .texOffs(0, 26).addBox(-2.5F, -13.0F, -2.5F, 5.0F, 5.0F, 5.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, (float) -(Math.PI * 70.5 / 180f), (float) (Math.PI * 120 / 180f), 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
	}

    @Override
    public void setupAnim(UnforgivenRidiculeRenderState state) {
        super.setupAnim(state);
        float angle;
        if(state.isCharging){
            this.branchAnimator.xRot = (float) (Math.PI * 0.5);
            angle = state.ageInTicks * (float) Math.PI * -0.25F;
            subBranches[0].xRot = (float) -(Math.PI * 45 / 180f);
            subBranches[1].xRot = (float) -(Math.PI * 45 / 180f);
            subBranches[2].xRot = (float) -(Math.PI * 45 / 180f);
        }else{
            angle = state.ageInTicks * (float) Math.PI * -0.1F;
            subBranches[0].xRot = (float) -(Math.PI * 70.5 / 180f);
            subBranches[1].xRot = (float) -(Math.PI * 70.5 / 180f);
            subBranches[2].xRot = (float) -(Math.PI * 70.5 / 180f);
        }
        subBranches[0].yRot = angle;
        subBranches[1].yRot = (float) -(Math.PI * 120 / 180f) + angle;
        subBranches[2].yRot = (float) (Math.PI * 120 / 180f) + angle;


        this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
        this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
    }
}