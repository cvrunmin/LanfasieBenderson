package io.github.cvrunmin.lanfasie.benderson.utils;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class ArmPoseEnumProxy {
    public static final EnumProxy<HumanoidModel.ArmPose> MANA_FOCI_POSE_PROXY = new EnumProxy<>(
            HumanoidModel.ArmPose.class, false, true, (IArmPoseTransformer) (model, state, armEnum) -> {
                var arm = armEnum == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
                int invert = armEnum == HumanoidArm.RIGHT ? 1 : -1;
                arm.yRot = -0.1F * invert + model.head.yRot;
                arm.xRot = (float) (-Math.PI / 2) + 0.3f;
                if (state.isFallFlying || state.swimAmount > 0.0F) {
                    arm.xRot -= 0.9599311F;
                }
            }
    );
}
