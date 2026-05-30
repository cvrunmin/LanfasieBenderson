package io.github.cvrunmin.lanfasie.benderson.content.marker;

import net.minecraft.client.model.animal.feline.AdultCatModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.util.Mth;

public class BlackCatSmashModel extends AdultCatModel {
    public BlackCatSmashModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(CatRenderState state) {
        super.setupAnim(state);
        float animationSpeed = state.walkAnimationSpeed;
        float animationPos = state.walkAnimationPos;
        this.leftHindLeg.xRot = Mth.cos(animationPos * 0.6662F) * animationSpeed;
        this.rightHindLeg.xRot = Mth.cos(animationPos * 0.6662F) * animationSpeed;
        this.leftFrontLeg.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * animationSpeed;
        this.rightFrontLeg.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * animationSpeed;
    }
}
