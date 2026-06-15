package io.github.cvrunmin.lanfasie.benderson.content.marker;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Function;

public class TargetMarkerRenderer extends EntityRenderer<TargetMarker, TargetMarkerRenderState> {
    private static final Identifier LETHAL_ATTACK_MARKER_TEXTURE = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/omen/lethal_attack_omen.png");
    private static final Identifier STACK_ATTACK_MARKER_TEXTURE = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/omen/stack_attack_omen.png");
    private static final Identifier COLOR_SCHEME_MARKER_TEXTURE = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/omen/color_scheme.png");
    private static final Identifier ARENA_FLAME = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/arena_flame.png");
    private static final Identifier RADIAL_KNOCKBACK_ARROW = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/omen/radial_knockback_omen.png");
    private static final Identifier GROUND_PROXIMITY_AIMER = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/omen/ground_proximity_aimer.png");

    private static final RenderPipeline.Snippet ATTACK_MARKER_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_LIGHT_DIR_SNIPPET)
            .withVertexShader("core/entity")
            .withFragmentShader("core/entity")
            .withSampler("Sampler0")
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false).buildSnippet();
    private static final Function<Identifier, RenderType> RENDER_TYPE = Util.memoize((location) -> RenderType.create("attack_target_marker", RenderSetup.builder(
                    RenderPipeline.builder(ATTACK_MARKER_SNIPPET)
                            .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
                            .withLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "attack_target_marker"))
                            .build())
            .withTexture("Sampler0", location).sortOnUpload().createRenderSetup()));

    private static final Function<Identifier, RenderType> TRIANGLE_STRIP_RENDER_TYPE = Util.memoize((location) -> RenderType.create("attack_target_marker_tstrip", RenderSetup.builder(
                    RenderPipeline.builder(ATTACK_MARKER_SNIPPET)
                            .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.TRIANGLE_STRIP)
                            .withLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "attack_target_marker_tstrip"))
                            .build())
            .withTexture("Sampler0", location).sortOnUpload().createRenderSetup()));

    private static final int LETHAL_ATTACK_ARROW_1_WIDTH = 103;
    private static final int LETHAL_ATTACK_ARROW_1_HEIGHT = 43;
    private static final int LETHAL_ATTACK_ARROW_2_WIDTH = 71;
    private static final int LETHAL_ATTACK_ARROW_2_HEIGHT = 43;

    public TargetMarkerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public TargetMarkerRenderState createRenderState() {
        return new TargetMarkerRenderState();
    }

    @Override
    public void extractRenderState(TargetMarker entity, TargetMarkerRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.markerType = entity.getMarkerArgs().markerType();
        state.lifeTimeInTick = (entity.getLifeTick() + partialTicks);
        state.expectedLifeTime = entity.getMarkerArgs().expectedLife();
        state.alwaysSee = entity.isPersistent();
        state.range = entity.getMarkerArgs().range();
        state.range2 = entity.getMarkerArgs().range2();
        state.direction = entity.getMarkerArgs().direction();
        state.isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();
        if(entity.getTargetType() == TargetMarker.TargetType.ENTITY){
            var targetEntity = entity.getTargetEntity();
            if(targetEntity != null){
                var lerpTargetPos = Mth.lerp(partialTicks, targetEntity.oldPosition(), targetEntity.position());
                state.x = lerpTargetPos.x;
                state.y = lerpTargetPos.y;
                state.z = lerpTargetPos.z;
                if(state.isFirstPerson){
                    state.markerHeight = Math.max(0, targetEntity.getEyeHeight() - 0.5f);
                }else{
                    state.markerHeight = Math.max(0, targetEntity.getBbHeight() / 2f);

                }
                state.overheadOffset = targetEntity.getBbHeight() - 0.25f;
            }
        }
        if(entity.getMarkerArgs().markerType() == TargetMarker.MarkerType.LINEAR_STACK){
            LivingEntity sourceEntity = entity.getSourceEntity();
            if(sourceEntity != null){
                state.direction = entity.getPosition(partialTicks).subtract(sourceEntity.getPosition(partialTicks)).multiply(1, 0, 1).normalize();
            }
        }
    }

    @Override
    protected boolean affectedByCulling(TargetMarker entity) {
        return false;
    }

    @Override
    public void submit(TargetMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);
        float alpha;
        if(!state.alwaysSee){
            var alpha1 = Mth.clamp(state.lifeTimeInTick / 5f, 0f, 1f);
            var alpha2 = Mth.clamp((state.expectedLifeTime - state.lifeTimeInTick) / 10f, 0f, 1f);
            alpha = alpha1 * alpha2;
        } else {
            alpha = 1f;
        }
        if(alpha < 0.1f) return; // skip rendering when nearly invisible
        switch (state.markerType) {
            case LETHAL_ATTACK -> submitLethalAttackMark(state, poseStack, submitNodeCollector, camera, alpha);
            case CIRCLE_AOE -> submitCircleAoeMark(state, poseStack, submitNodeCollector, camera, alpha);
            case LINEAR_AOE -> submitLinearAoeMark(state, poseStack, submitNodeCollector, camera, alpha);
            case CONE_AOE -> submitConeAoeMark(state, poseStack, submitNodeCollector, camera, alpha);
            case CIRCLE_STACK -> submitCircleStackMark(state, poseStack, submitNodeCollector, camera, alpha);
            case LINEAR_STACK -> submitLinearStackMark(state, poseStack, submitNodeCollector, camera, alpha);
            case ARENA_HINT -> submitArenaHint(state, poseStack, submitNodeCollector, camera, alpha);
            case GROUND_PROXIMITY -> submitGroundProximityAoe(state, poseStack, submitNodeCollector, camera, alpha);
            case KNOCKBACK_RADIAL -> submitRadialKnockback(state, poseStack, submitNodeCollector, camera, alpha);
            case null, default -> {
            }
        }
    }

    private void submitLethalAttackMark(TargetMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, float alpha) {
        submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE.apply(LETHAL_ATTACK_MARKER_TEXTURE), (inPose, buffer) -> {
            var innerStack = new PoseStack();
            innerStack.last().set(inPose);
            innerStack.pushPose();
            innerStack.rotateAround(new Quaternionf().rotateY((float) (Math.PI * state.ageInTicks * 0.05)), 0, 0, 0);
            innerStack.translate(0, state.markerHeight, 0);
            var pose = innerStack.last();
            buffer.addVertex(pose, -1, 0, -1).setUv(0, 0).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 1, 0);
            buffer.addVertex(pose, -1, 0, 1).setUv(0, 1).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 1, 0);
            buffer.addVertex(pose, 1, 0, 1).setUv(0.5f, 1).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 1, 0);
            buffer.addVertex(pose, 1, 0, -1).setUv(0.5f, 0).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 1, 0);
            innerStack.popPose();
            innerStack.pushPose();
            innerStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-camera.yRot)), 0, 0, 0);
            innerStack.translate(0, state.overheadOffset + 0.5f, 0);
            pose = innerStack.last();
            buffer.addVertex(pose, -0.5f, 0.4f, 0).setUv(0.5f, 0).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, -0.5f, 0, 0).setUv(0.5f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.5f, 0, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_1_WIDTH / 512f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.5f, 0.4f, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_1_WIDTH / 512f, 0).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);

            innerStack.translate(0, 0.2f + 0.2f * ((float) Math.cos(Math.PI * state.ageInTicks * 0.1) + 1), 0);
            pose = innerStack.last();
            buffer.addVertex(pose, -0.25f, 0.4f, 0).setUv(0.5f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, -0.25f, 0, 0).setUv(0.5f, (LETHAL_ATTACK_ARROW_1_HEIGHT + LETHAL_ATTACK_ARROW_2_HEIGHT) / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.25f, 0, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_2_WIDTH / 512f, (LETHAL_ATTACK_ARROW_1_HEIGHT + LETHAL_ATTACK_ARROW_2_HEIGHT) / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.25f, 0.4f, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_2_WIDTH / 512f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, -0.25f, 0.7f, 0).setUv(0.5f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, -0.25f, 0.3f, 0).setUv(0.5f, (LETHAL_ATTACK_ARROW_1_HEIGHT + LETHAL_ATTACK_ARROW_2_HEIGHT) / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.25f, 0.3f, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_2_WIDTH / 512f, (LETHAL_ATTACK_ARROW_1_HEIGHT + LETHAL_ATTACK_ARROW_2_HEIGHT) / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.25f, 0.7f, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_2_WIDTH / 512f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            innerStack.popPose();
        });
    }

    private void submitCircleAoeMark(TargetMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, float alpha) {
        float t1 = state.ageInTicks % 20;
        float a1;
        if(t1 < 5){
            a1 = t1 / 5f * 0.8f;
        }else if(t1 < 15){
            a1 = 0.8f;
        }else{
            a1 = (20 - t1) / 5f * 0.8f;
        }
        submitNodeCollector.submitCustomGeometry(poseStack, TRIANGLE_STRIP_RENDER_TYPE.apply(COLOR_SCHEME_MARKER_TEXTURE), (inPose, buffer) -> {
            var innerStack = new PoseStack();
            var halfRange = state.range * 0.5f;
            buffer.addVertex(inPose, Math.max(0, halfRange - 0.25f), 1e-3f, 0).setColor(1f, 1f, 1f, alpha).setUv(0.9999f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            for (int i = 0; i < 361; i++) {
                var rad = Math.PI * i / 180f;
                var cr = (float)Math.cos(rad);
                var sr = (float)Math.sin(rad);
                buffer.addVertex(inPose, Math.max(0, halfRange - 0.25f) * cr, 1e-3f, Math.max(0, halfRange - 0.25f) * sr).setColor(1f, 1f, 1f, alpha).setUv(0.9999f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                buffer.addVertex(inPose, halfRange * cr, 1e-3f, halfRange * sr).setColor(1f, 1f, 1f, alpha).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            }
            for (int i = 0; i < 361; i++) {
                var rad = Math.PI * i / 180f;
                var cr = (float)Math.cos(rad);
                var sr = (float)Math.sin(rad);
                buffer.addVertex(inPose, 0, 1e-3f, 0).setColor(1f, 1f, 1f, alpha).setUv(0.999f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                buffer.addVertex(inPose, Math.max(0, halfRange - 0.25f) * cr, 1e-3f, Math.max(0, halfRange - 0.25f) * sr).setColor(1f, 1f, 1f, alpha).setUv(0.999f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            }
            buffer.addVertex(inPose, Math.max(0, halfRange - 0.25f), 1e-3f, 0).setColor(1f, 1f, 1f, alpha).setUv(0.999f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
        });
        submitNodeCollector.submitCustomGeometry(poseStack, TRIANGLE_STRIP_RENDER_TYPE.apply(COLOR_SCHEME_MARKER_TEXTURE), (inPose, buffer) -> {
            var innerStack = new PoseStack();
            var halfRange = state.range * 0.5f * (t1 / 20f);
            buffer.addVertex(inPose, Math.max(0, halfRange - 0.125f), 2e-3f, 0).setColor(1f, 1f, 1f, alpha * a1).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            for (int i = 0; i < 361; i++) {
                var rad = Math.PI * i / 180f;
                var cr = (float)Math.cos(rad);
                var sr = (float)Math.sin(rad);
                buffer.addVertex(inPose, Math.max(0, halfRange - 0.125f) * cr, 2e-3f, Math.max(0, halfRange - 0.125f) * sr).setColor(1f, 1f, 1f, alpha * a1).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                buffer.addVertex(inPose, halfRange * cr, 2e-3f, halfRange * sr).setColor(1f, 1f, 1f, alpha * a1).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            }
            buffer.addVertex(inPose, halfRange, 2e-3f, 0).setColor(1f, 1f, 1f, alpha * a1).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
        });
    }

    private void submitLinearAoeMark(TargetMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, float alpha){
        float t1 = state.ageInTicks % 20;
        float a1;
        if(t1 < 5){
            a1 = t1 / 5f * 0.8f;
        }else if(t1 < 15){
            a1 = 0.8f;
        }else{
            a1 = (20 - t1) / 5f * 0.8f;
        }
        poseStack.pushPose();
        poseStack.rotateAround(new Quaternionf().rotationTo(new Vector3f(0, 0, 1), state.direction.horizontal().normalize().toVector3f()), 0, 0, 0);
        submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE.apply(COLOR_SCHEME_MARKER_TEXTURE), (inPose, buffer) -> {
            var innerStack = new PoseStack();
            var halfRange = state.range * 0.5f;
            var halfRange2 = state.range2 * 0.5f;
            var range2 = state.range2;
            buffer.addVertex(inPose, -halfRange, 1e-3f, 0).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, -halfRange, 1e-3f, Math.min(halfRange2, 0.25f)).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, -halfRange + 0.25f, 1e-3f, Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, -halfRange + 0.25f, 1e-3f, 0).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);

            buffer.addVertex(inPose, -halfRange + 0.25f, 1e-3f, 0).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, -halfRange + 0.25f, 1e-3f, Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange - 0.25f, 1e-3f, Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange - 0.25f, 1e-3f, 0).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);

            buffer.addVertex(inPose, halfRange - 0.25f, 1e-3f, 0).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange - 0.25f, 1e-3f, Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange, 1e-3f, Math.min(halfRange2, 0.25f)).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange, 1e-3f, 0).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);

            buffer.addVertex(inPose, halfRange - 0.25f, 1e-3f, Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange - 0.25f, 1e-3f, range2 - Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange, 1e-3f,range2 - Math.min(halfRange2, 0.25f)).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange, 1e-3f, Math.min(halfRange2, 0.25f)).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);

            buffer.addVertex(inPose, -halfRange + 0.25f, 1e-3f, Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, -halfRange + 0.25f, 1e-3f, range2 - Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange - 0.25f, 1e-3f, range2 - Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange - 0.25f, 1e-3f, Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);

            buffer.addVertex(inPose, -halfRange, 1e-3f, Math.min(halfRange2, 0.25f)).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, -halfRange, 1e-3f, range2 - Math.min(halfRange2, 0.25f)).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, -halfRange + 0.25f, 1e-3f, range2 - Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, -halfRange + 0.25f, 1e-3f, Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);

            buffer.addVertex(inPose, -halfRange, 1e-3f, range2 - Math.min(halfRange2, 0.25f)).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, -halfRange, 1e-3f, range2).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, -halfRange + 0.25f, 1e-3f, range2).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, -halfRange + 0.25f, 1e-3f, range2 - Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);

            buffer.addVertex(inPose, -halfRange + 0.25f, 1e-3f, range2 - Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, -halfRange + 0.25f, 1e-3f, range2).setUv(0, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange - 0.25f, 1e-3f, range2).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange - 0.25f, 1e-3f, range2 - Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);

            buffer.addVertex(inPose, halfRange - 0.25f, 1e-3f, range2 - Math.min(halfRange2, 0.25f)).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange - 0.25f, 1e-3f, range2).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange, 1e-3f, range2).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange, 1e-3f, range2 - Math.min(halfRange2, 0.25f)).setUv(0f, 0f).setColor(1, 1, 1, alpha).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
        });
        submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE.apply(COLOR_SCHEME_MARKER_TEXTURE), (inPose, buffer) -> {
            var innerStack = new PoseStack();
            var dr2 = state.range2 * (t1 / 20f) - 0.25f;
            var halfRange = state.range * 0.5f;
            var halfRange2 = state.range2 * 0.5f;
            buffer.addVertex(inPose, -halfRange, 2e-3f, dr2).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha * a1).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, -halfRange, 2e-3f, dr2 + Math.min(halfRange2, 0.25f)).setUv(0f, 0f).setColor(1, 1, 1, alpha * a1).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange, 2e-3f, dr2 + Math.min(halfRange2, 0.25f)).setUv(0f, 0f).setColor(1, 1, 1, alpha * a1).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(inPose, halfRange, 2e-3f, dr2).setUv(0.9999f, 0f).setColor(1, 1, 1, alpha * a1).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
        });
        poseStack.popPose();
    }

    private void submitConeAoeMark(TargetMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, float alpha){
        var angle = state.range2 % 360;
        if(angle == 0) return;
        var halfAngle = angle * 0.5f;
        float t1 = state.ageInTicks % 20;
        float a1;
        if(t1 < 5){
            a1 = t1 / 5f * 0.8f;
        }else if(t1 < 15){
            a1 = 0.8f;
        }else{
            a1 = (20 - t1) / 5f * 0.8f;
        }
        poseStack.pushPose();
        poseStack.rotateAround(new Quaternionf().rotationTo(new Vector3f(1, 0, 0), state.direction.horizontal().normalize().toVector3f()), 0, 0, 0);
        submitNodeCollector.submitCustomGeometry(poseStack, TRIANGLE_STRIP_RENDER_TYPE.apply(COLOR_SCHEME_MARKER_TEXTURE), (inPose, buffer) -> {
            var innerStack = new PoseStack();
            var halfRange = state.range;
            for (int i = 0; i < angle + 1; i++) {
                float fi = i;
                if(fi > angle) fi = angle;
                fi -= halfAngle;
                var rad = Math.PI * fi / 180f;
                var cr = (float)Math.cos(rad);
                var sr = (float)Math.sin(rad);
                if(i == 0){
                    buffer.addVertex(inPose, Math.max(0, halfRange - 0.25f) * cr, 1e-3f, Math.max(0, halfRange - 0.25f) * sr).setColor(1f, 1f, 1f, alpha).setUv(0.9999f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                }
                buffer.addVertex(inPose, Math.max(0, halfRange - 0.25f) * cr, 1e-3f, Math.max(0, halfRange - 0.25f) * sr).setColor(1f, 1f, 1f, alpha).setUv(0.9999f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                buffer.addVertex(inPose, halfRange * cr, 1e-3f, halfRange * sr).setColor(1f, 1f, 1f, alpha).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                if(i + 1 >= angle + 1){
                    buffer.addVertex(inPose, halfRange * cr, 1e-3f, halfRange * sr).setColor(1f, 1f, 1f, alpha).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                }
            }
            for (int i = 0; i < angle + 1; i++) {
                float fi = i;
                if(fi > angle) fi = angle;
                fi -= halfAngle;
                var rad = Math.PI * fi / 180f;
                var cr = (float)Math.cos(rad);
                var sr = (float)Math.sin(rad);
                if(i == 0){
                    buffer.addVertex(inPose, 0, 1e-3f, 0).setColor(1f, 1f, 1f, alpha).setUv(0.999f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                }
                buffer.addVertex(inPose, 0, 1e-3f, 0).setColor(1f, 1f, 1f, alpha).setUv(0.999f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                buffer.addVertex(inPose, Math.max(0, halfRange - 0.25f) * cr, 1e-3f, Math.max(0, halfRange - 0.25f) * sr).setColor(1f, 1f, 1f, alpha).setUv(0.999f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                if(i + 1 >= angle + 1){
                    buffer.addVertex(inPose, Math.max(0, halfRange - 0.25f) * cr, 1e-3f, Math.max(0, halfRange - 0.25f) * sr).setColor(1f, 1f, 1f, alpha).setUv(0.999f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                }
            }
        });
        submitNodeCollector.submitCustomGeometry(poseStack, TRIANGLE_STRIP_RENDER_TYPE.apply(COLOR_SCHEME_MARKER_TEXTURE), (inPose, buffer) -> {
            var innerStack = new PoseStack();
            var halfRange = state.range * (t1 / 20f);
            for (int i = 0; i < angle + 1; i++) {
                float fi = i;
                if(fi > angle) fi = angle;
                fi -= halfAngle;
                var rad = Math.PI * fi / 180f;
                var cr = (float)Math.cos(rad);
                var sr = (float)Math.sin(rad);
                if(i == 0){
                    buffer.addVertex(inPose, Math.max(0, halfRange - 0.125f) * cr, 3e-3f, Math.max(0, halfRange - 0.125f) * sr).setColor(1f, 1f, 1f, alpha * a1).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                }
                buffer.addVertex(inPose, Math.max(0, halfRange - 0.125f) * cr, 3e-3f, Math.max(0, halfRange - 0.125f) * sr).setColor(1f, 1f, 1f, alpha * a1).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                buffer.addVertex(inPose, halfRange * cr, 3e-3f, halfRange * sr).setColor(1f, 1f, 1f, alpha * a1).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                if(i + 1 >= angle + 1){
                    buffer.addVertex(inPose, halfRange * cr, 3e-3f, halfRange * sr).setColor(1f, 1f, 1f, alpha * a1).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                }
            }
        });
        poseStack.popPose();
    }

    private void submitCircleStackMark(TargetMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, float alpha){
        submitNodeCollector.submitCustomGeometry(poseStack, TRIANGLE_STRIP_RENDER_TYPE.apply(STACK_ATTACK_MARKER_TEXTURE), (inPose, buffer) -> {
            var innerStack = new PoseStack();
            innerStack.last().set(inPose);
            innerStack.pushPose();
            innerStack.rotateAround(new Quaternionf().rotateY((float) (Math.PI * state.ageInTicks * 0.05)), 0, 0, 0);
            innerStack.translate(0, state.markerHeight, 0);
            var pose = innerStack.last();
            var halfRange = state.range * 0.5f;
            buffer.addVertex(pose, Math.max(0, halfRange - 0.125f), 1e-3f, 0).setColor(1f, 1f, 1f, alpha).setUv((129 + 121) / 512f, (129) / 256f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            for (int i = 0; i < 361; i++) {
                var rad = Math.PI * i / 180f;
                var cr = (float)Math.cos(rad);
                var sr = (float)Math.sin(rad);
                buffer.addVertex(pose, Math.max(0, halfRange - 0.125f) * cr, 1e-3f, Math.max(0, halfRange - 0.125f) * sr).setColor(1f, 1f, 1f, alpha).setUv((129 + 121 * cr) / 512f, (129 + 121 * sr) / 256f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                buffer.addVertex(pose, halfRange * cr, 1e-3f, halfRange * sr).setColor(1f, 1f, 1f, alpha).setUv((129 + 125 * cr) / 512f, (129 + 125 * sr) / 256f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            }
            buffer.addVertex(pose, 0, 1e-3f, 0).setColor(1f, 1f, 1f, alpha).setUv(129 / 512f, 129 / 256f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            buffer.addVertex(pose, 0, 1e-3f, 0).setColor(1f, 1f, 1f, alpha).setUv(129 / 512f, 129 / 256f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            innerStack.popPose();
        });
        submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE.apply(STACK_ATTACK_MARKER_TEXTURE), (inPose, buffer) -> {
            var innerStack = new PoseStack();
            innerStack.last().set(inPose);
            innerStack.pushPose();
            innerStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-camera.yRot)), 0, 0, 0);
            innerStack.translate(0, state.overheadOffset + 0.5f, 0);
            var pose = innerStack.last();
            buffer.addVertex(pose, -0.5f, 0.4f, 0).setUv(0.5f, 0).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, -0.5f, 0, 0).setUv(0.5f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.5f, 0, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_1_WIDTH / 512f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.5f, 0.4f, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_1_WIDTH / 512f, 0).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);

            innerStack.translate(0, 0.2f + 0.2f * ((float) Math.cos(Math.PI * state.ageInTicks * 0.1) + 1), 0);
            pose = innerStack.last();
            buffer.addVertex(pose, -0.25f, 0.4f, 0).setUv(0.5f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, -0.25f, 0, 0).setUv(0.5f, (LETHAL_ATTACK_ARROW_1_HEIGHT + LETHAL_ATTACK_ARROW_2_HEIGHT) / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.25f, 0, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_2_WIDTH / 512f, (LETHAL_ATTACK_ARROW_1_HEIGHT + LETHAL_ATTACK_ARROW_2_HEIGHT) / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.25f, 0.4f, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_2_WIDTH / 512f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, -0.25f, 0.7f, 0).setUv(0.5f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, -0.25f, 0.3f, 0).setUv(0.5f, (LETHAL_ATTACK_ARROW_1_HEIGHT + LETHAL_ATTACK_ARROW_2_HEIGHT) / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.25f, 0.3f, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_2_WIDTH / 512f, (LETHAL_ATTACK_ARROW_1_HEIGHT + LETHAL_ATTACK_ARROW_2_HEIGHT) / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.25f, 0.7f, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_2_WIDTH / 512f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            innerStack.popPose();
            var halfRange = 2.0f;
            for (int i1 = 0; i1 < 4; i1++) {
                innerStack.pushPose();
                if(state.isFirstPerson){
                    innerStack.rotateAround(new Quaternionf().rotateY((float) (Math.toRadians(-camera.yRot) + Math.PI * 0.25)), 0, 0, 0);
                }
                innerStack.rotateAround(new Quaternionf().rotateY((float) (Math.PI * 0.5 * i1)), 0, 0, 0);
                innerStack.translate(halfRange, state.markerHeight + 0.01f, 0);
                pose = innerStack.last();
                for (int i2 = 0; i2 < 5; i2++) {
                    float t2 = ((state.ageInTicks + i2 * 2) % 20);
                    float deltaRadius = (float) (2.0 * (1 - (1 - Math.pow(1 - t2 / 20f, 5))));
                    float alpha3 = t2 < 15 ? 1 : (20 - t2) / 5;
                    float alpha4 = t2 < 10 ? t2 * 0.7f / 10 + 0.3f : 1;
                    buffer.addVertex(pose, 0.75f + deltaRadius, 0, -0.625f).setUv(0.5f, 0).setUv1(0, 0).setUv2(0, 0).setColor(alpha4, 1f, alpha4, alpha * alpha3).setNormal(0, 1, 0);
                    buffer.addVertex(pose, 0 + deltaRadius, 0, -0.625f).setUv(0.5f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(alpha4, 1f, alpha4, alpha * alpha3).setNormal(0, 1, 0);
                    buffer.addVertex(pose, 0 + deltaRadius, 0, 0.625f).setUv(0.5f + LETHAL_ATTACK_ARROW_1_WIDTH / 512f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(alpha4, 1f, alpha4, alpha * alpha3).setNormal(0, 1, 0);
                    buffer.addVertex(pose, 0.75f + deltaRadius, 0.0f, 0.625f).setUv(0.5f + LETHAL_ATTACK_ARROW_1_WIDTH / 512f, 0).setUv1(0, 0).setUv2(0, 0).setColor(alpha4, 1f, alpha4, alpha * alpha3).setNormal(0, 1, 0);
                }
                innerStack.popPose();
            }
        });
    }

    private void submitLinearStackMark(TargetMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, float alpha) {
        submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE.apply(STACK_ATTACK_MARKER_TEXTURE), (inPose, buffer) -> {
            var innerStack = new PoseStack();
            innerStack.last().set(inPose);
            innerStack.pushPose();
            innerStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-camera.yRot)), 0, 0, 0);
            innerStack.translate(0, state.overheadOffset + 0.5f, 0);
            var pose = innerStack.last();
            buffer.addVertex(pose, -0.5f, 0.4f, 0).setUv(0.5f, 0).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, -0.5f, 0, 0).setUv(0.5f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.5f, 0, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_1_WIDTH / 512f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.5f, 0.4f, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_1_WIDTH / 512f, 0).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);

            innerStack.translate(0, 0.2f + 0.2f * ((float) Math.cos(Math.PI * state.ageInTicks * 0.1) + 1), 0);
            pose = innerStack.last();
            buffer.addVertex(pose, -0.25f, 0.4f, 0).setUv(0.5f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, -0.25f, 0, 0).setUv(0.5f, (LETHAL_ATTACK_ARROW_1_HEIGHT + LETHAL_ATTACK_ARROW_2_HEIGHT) / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.25f, 0, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_2_WIDTH / 512f, (LETHAL_ATTACK_ARROW_1_HEIGHT + LETHAL_ATTACK_ARROW_2_HEIGHT) / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.25f, 0.4f, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_2_WIDTH / 512f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, -0.25f, 0.7f, 0).setUv(0.5f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, -0.25f, 0.3f, 0).setUv(0.5f, (LETHAL_ATTACK_ARROW_1_HEIGHT + LETHAL_ATTACK_ARROW_2_HEIGHT) / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.25f, 0.3f, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_2_WIDTH / 512f, (LETHAL_ATTACK_ARROW_1_HEIGHT + LETHAL_ATTACK_ARROW_2_HEIGHT) / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            buffer.addVertex(pose, 0.25f, 0.7f, 0).setUv(0.5f + LETHAL_ATTACK_ARROW_2_WIDTH / 512f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 0, 1);
            innerStack.popPose();

            var halfRange = state.range * 0.5f;
            for (int i1 = 0; i1 < 6; i1++) {
                var zOffset = (i1 % 3 - 1) * 1.45f;
                var xScale = i1 / 3 == 1 ? 1 : -1;
                innerStack.pushPose();
                innerStack.rotateAround(new Quaternionf().rotationTo(new Vector3f(0, 0, 1), state.direction.toVector3f()), 0, 0, 0);
                innerStack.scale(xScale, 1, 1);
                innerStack.translate(halfRange, state.markerHeight + 0.01f, zOffset);
                pose = innerStack.last();
                for (int i2 = 0; i2 < 5; i2++) {
                    float t2 = ((state.ageInTicks + i2 * 2) % 20);
                    float deltaRadius = (float) (2.0 * (1 - (1 - Math.pow(1 - t2 / 20f, 5))));
                    float alpha3 = t2 < 15 ? 1 : (20 - t2) / 5;
                    float alpha4 = t2 < 10 ? t2 * 0.7f / 10 + 0.3f : 1;
                    buffer.addVertex(pose, 0.75f + deltaRadius, 0, -0.625f).setUv(0.5f, 0).setUv1(0, 0).setUv2(0, 0).setColor(alpha4, 1f, alpha4, alpha * alpha3).setNormal(0, 1, 0);
                    buffer.addVertex(pose, 0 + deltaRadius, 0, -0.625f).setUv(0.5f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(alpha4, 1f, alpha4, alpha * alpha3).setNormal(0, 1, 0);
                    buffer.addVertex(pose, 0 + deltaRadius, 0, 0.625f).setUv(0.5f + LETHAL_ATTACK_ARROW_1_WIDTH / 512f, LETHAL_ATTACK_ARROW_1_HEIGHT / 256f).setUv1(0, 0).setUv2(0, 0).setColor(alpha4, 1f, alpha4, alpha * alpha3).setNormal(0, 1, 0);
                    buffer.addVertex(pose, 0.75f + deltaRadius, 0.0f, 0.625f).setUv(0.5f + LETHAL_ATTACK_ARROW_1_WIDTH / 512f, 0).setUv1(0, 0).setUv2(0, 0).setColor(alpha4, 1f, alpha4, alpha * alpha3).setNormal(0, 1, 0);
                }
                innerStack.popPose();
            }
        });
    }

    private void submitArenaHint(TargetMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, float alpha){
        var arenaRadius = state.range;
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(ARENA_FLAME), (inPose, buffer) -> {
            var inStack = new PoseStack();
            inStack.last().set(inPose);
            for (int i = 0; i < 4; i++) {
                inStack.pushPose();
                inStack.rotateAround(new Quaternionf().rotationY((float) (Math.PI * i / 2f)), 0, 0, 0);
                var pose1 = inStack.last();
                buffer.addVertex(pose1, -arenaRadius, 1.5f, -arenaRadius).setUv(0, 0).setColor(0xffffffff).setLight(LightCoordsUtil.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose1, 0, 0, 1);
                buffer.addVertex(pose1, -arenaRadius, -1.5f, -arenaRadius).setUv(0, 1).setColor(0xffffffff).setLight(LightCoordsUtil.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose1, 0, 0, 1);
                buffer.addVertex(pose1, arenaRadius, -1.5f, -arenaRadius).setUv(1, 1).setColor(0xffffffff).setLight(LightCoordsUtil.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose1, 0, 0, 1);
                buffer.addVertex(pose1, arenaRadius, 1.5f, -arenaRadius).setUv(1, 0).setColor(0xffffffff).setLight(LightCoordsUtil.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose1, 0, 0, 1);
                inStack.popPose();
            }
        });
    }

    private void submitRadialKnockback(TargetMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, float alpha){
        var halfRadius = ((int) Math.ceil(state.range * 0.5f));
        for (int i = 1; i < halfRadius; i++) {
            float iOffsetted = i + (state.ageInTicks % 10 / 10.0f) - 0.5f;
            poseStack.pushPose();
            float alpha1;
            if(i == 1){
                alpha1 = Math.clamp(1 - (i - iOffsetted) / 0.5f, 0, 1);
            } else if (i == halfRadius - 1) {
                alpha1 = Math.clamp(1 - (iOffsetted - i) / 0.5f, 0, 1);
            }else{
                alpha1 = 1;
            }
            submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE.apply(RADIAL_KNOCKBACK_ARROW), (inPose, buffer) -> {
                var poseStack1 = new PoseStack();
                poseStack1.last().set(inPose);
                for (int j = 0; j < 16; j++) {
                    poseStack1.pushPose();
                    poseStack1.rotateAround(new Quaternionf().rotationY((float) (Math.PI * j / 8.0)), 0, 0, 0);
                    float x1 = (float) (iOffsetted * Math.cos(Math.PI * 7 / 128));
                    float x2 = x1 * (iOffsetted + 1) / iOffsetted;
                    float z1 = (float) (iOffsetted * Math.sin(Math.PI * 7 / 128));
                    float z2 = z1 * (iOffsetted + 1) / iOffsetted;
                    var pose = poseStack1.last();
                    buffer.addVertex(pose, x1, 1e-3f, -z1).setUv(1, 0).setColor(1f, 1f, 1f, alpha * alpha1).setLight(LightCoordsUtil.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0, 1, 0);
                    buffer.addVertex(pose, x1, 1e-3f, z1).setUv(0, 0).setColor(1f, 1f, 1f, alpha * alpha1).setLight(LightCoordsUtil.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0, 1, 0);
                    buffer.addVertex(pose, x2, 1e-3f, z2).setUv(0, 1).setColor(1f, 1f, 1f, alpha * alpha1).setLight(LightCoordsUtil.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0, 1, 0);
                    buffer.addVertex(pose, x2, 1e-3f, -z2).setUv(1, 1).setColor(1f, 1f, 1f, alpha * alpha1).setLight(LightCoordsUtil.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0, 1, 0);
                    poseStack1.popPose();
                }
            });
            poseStack.popPose();
        }
    }

    private void submitGroundProximityAoe(TargetMarkerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, float alpha){
        float t1 = state.ageInTicks % 20;
        float a1;
        if(t1 < 5){
            a1 = t1 / 5f * 0.8f;
        }else if(t1 < 15){
            a1 = 0.8f;
        }else{
            a1 = (20 - t1) / 5f * 0.8f;
        }
        submitNodeCollector.submitCustomGeometry(poseStack, RENDER_TYPE.apply(GROUND_PROXIMITY_AIMER), (inPose, buffer) -> {
            var innerStack = new PoseStack();
            innerStack.last().set(inPose);
            innerStack.pushPose();
            innerStack.rotateAround(new Quaternionf().rotateY((float) (Math.PI * state.ageInTicks * 0.05)), 0, 0, 0);
            innerStack.translate(0, 1e-3f, 0);
            var pose = innerStack.last();
            buffer.addVertex(pose, -1, 0, -1).setUv(0, 0).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 1, 0);
            buffer.addVertex(pose, -1, 0, 1).setUv(0, 1).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 1, 0);
            buffer.addVertex(pose, 1, 0, 1).setUv(1, 1).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 1, 0);
            buffer.addVertex(pose, 1, 0, -1).setUv(1, 0).setUv1(0, 0).setUv2(0, 0).setColor(1f, 1f, 1f, alpha).setNormal(0, 1, 0);
            innerStack.popPose();
        });
        submitNodeCollector.submitCustomGeometry(poseStack, TRIANGLE_STRIP_RENDER_TYPE.apply(COLOR_SCHEME_MARKER_TEXTURE), (inPose, buffer) -> {
            var innerStack = new PoseStack();
            var halfRange = state.range * 0.5f * (t1 / 20f);
            buffer.addVertex(inPose, Math.max(0, halfRange - 0.125f), 2e-3f, 0).setColor(1f, 1f, 1f, alpha * a1).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            for (int i = 0; i < 361; i++) {
                var rad = Math.PI * i / 180f;
                var cr = (float)Math.cos(rad);
                var sr = (float)Math.sin(rad);
                buffer.addVertex(inPose, Math.max(0, halfRange - 0.125f) * cr, 2e-3f, Math.max(0, halfRange - 0.125f) * sr).setColor(1f, 1f, 1f, alpha * a1).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
                buffer.addVertex(inPose, halfRange * cr, 2e-3f, halfRange * sr).setColor(1f, 1f, 1f, alpha * a1).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
            }
            buffer.addVertex(inPose, halfRange, 2e-3f, 0).setColor(1f, 1f, 1f, alpha * a1).setUv(0f, 0f).setUv1(0, 0).setUv2(0, 0).setNormal(0, 1, 0);
        });
    }
}
