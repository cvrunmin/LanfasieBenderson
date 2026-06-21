package io.github.cvrunmin.lanfasie.benderson.foundation;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.LevelSpecialPerformanceHandler;
import io.github.cvrunmin.lanfasie.benderson.mixin.LevelRendererAccessor;
import io.github.cvrunmin.lanfasie.benderson.mixin.SkyRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.CustomSkyboxRenderer;
import org.joml.*;

import java.lang.Math;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class HeiTideSkyboxRenderer implements CustomSkyboxRenderer {
    private static final Identifier GREEN_EYE_SPRITE = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "hei_tide_eye_green");
    private static final Identifier BLUE_EYE_SPRITE = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "hei_tide_eye_blue");

    public static final RenderPipeline EYES = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "pipeline/heitide_eyes"))
                    .withVertexShader("core/position_tex")
                    .withFragmentShader("core/position_tex")
                    .withSampler("Sampler0")
//                    .withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY))
                    .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
                    .build();

    private final RenderSystem.AutoStorageIndexBuffer quadIndices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
    private TextureAtlas celestialsAtlas;
    private GpuBuffer smallEyesBuffer;
    private GpuBuffer mainEyeBuffer;
    private int starIndexCount;
    private MappableRingBuffer fogBuffer;

    public void reloadResources(){
        if(smallEyesBuffer != null){
            smallEyesBuffer.close();
        }
        if(mainEyeBuffer != null){
            mainEyeBuffer.close();
        }
        if(fogBuffer != null){
            fogBuffer.close();
        }
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        AtlasManager atlasManager = Minecraft.getInstance().getAtlasManager();
        celestialsAtlas = atlasManager.getAtlasOrThrow(AtlasIds.CELESTIALS);
        smallEyesBuffer = buildSmallEyes();
        mainEyeBuffer = buildMainEye();
        fogBuffer = new MappableRingBuffer(() -> "Fog UBO", 130, FogRenderer.FOG_UBO_SIZE);
    }

    public void close(){
        if(smallEyesBuffer != null) {
            smallEyesBuffer.close();
        }
        if(mainEyeBuffer != null){
            mainEyeBuffer.close();
        }
        if(fogBuffer != null){
            fogBuffer.close();
        }
    }

    private GpuBuffer buildSmallEyes(){
        RandomSource random = RandomSource.createThreadLocalInstance(981757L);
        float starDistance = 100.0F;
        try (ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION_TEX.getVertexSize() * 1000 * 4)) {
            BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

            for (int i = 0; i < 1000; i++) {
                float x = random.nextFloat() * 2.0F - 1.0F;
                float y = Math.abs(random.nextFloat() * 2.0F - 1.0F);
                float z = random.nextFloat() * 2.0F - 1.0F;
                float eyeSize = (0.3F + random.nextFloat() * 0.2F) * 2.5f;
                float lengthSq = Mth.lengthSquared(x, y, z);
                if (lengthSq > 0.010000001F && lengthSq < 1.0F) {
                    Vector3f starCenter = new Vector3f(x, y, z).normalize(starDistance);
                    float zRot = (float)(random.nextDouble() * (float) Math.PI * 2.0);
                    var spriteLocation = random.nextBoolean() ? GREEN_EYE_SPRITE : BLUE_EYE_SPRITE;
                    var sprite = celestialsAtlas.getSprite(spriteLocation);
                    Matrix3f rotation = new Matrix3f().rotateTowards(new Vector3f(starCenter).negate(), new Vector3f(0.0F, 1.0F, 0.0F)).rotateZ(-zRot);
                    bufferBuilder.addVertex(new Vector3f(eyeSize, -eyeSize, 0.0F).mul(rotation).add(starCenter)).setUv(sprite.getU0(), sprite.getV0());
                    bufferBuilder.addVertex(new Vector3f(eyeSize, eyeSize, 0.0F).mul(rotation).add(starCenter)).setUv(sprite.getU0(), sprite.getV1());
                    bufferBuilder.addVertex(new Vector3f(-eyeSize, eyeSize, 0.0F).mul(rotation).add(starCenter)).setUv(sprite.getU1(), sprite.getV1());
                    bufferBuilder.addVertex(new Vector3f(-eyeSize, -eyeSize, 0.0F).mul(rotation).add(starCenter)).setUv(sprite.getU1(), sprite.getV0());
                }
            }

            try (MeshData mesh = bufferBuilder.buildOrThrow()) {
                this.starIndexCount = mesh.drawState().indexCount();
                return RenderSystem.getDevice().createBuffer(() -> "Small eyes vertex buffer", GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer());
            }
        }
    }


    private GpuBuffer buildMainEye() {
        var sprite = celestialsAtlas.getSprite(BLUE_EYE_SPRITE);

        try (ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(4 * DefaultVertexFormat.POSITION_TEX.getVertexSize())) {
            BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferBuilder.addVertex(-1.0F, 0.0F, -1.0F).setUv(sprite.getU0(), sprite.getV0());
            bufferBuilder.addVertex(1.0F, 0.0F, -1.0F).setUv(sprite.getU1(), sprite.getV0());
            bufferBuilder.addVertex(1.0F, 0.0F, 1.0F).setUv(sprite.getU1(), sprite.getV1());
            bufferBuilder.addVertex(-1.0F, 0.0F, 1.0F).setUv(sprite.getU0(), sprite.getV1());

            try (MeshData mesh = bufferBuilder.buildOrThrow()) {
                return RenderSystem.getDevice().createBuffer(() -> "Main eye quad", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer());
            }
        }
    }

    private boolean shouldOverrideFog(){
        return false;
    }

    private void renderSkyDisc(int skyColor) {
        LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;
        var skyRenderer = ((LevelRendererAccessor) levelRenderer).getSkyRenderer();
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrix(), ARGB.vector4fFromARGB32(skyColor), new Vector3f(), new Matrix4f());
        GpuTextureView colorTexture = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        GpuTextureView depthTexture = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "Sky disc", colorTexture, OptionalInt.empty(), depthTexture, OptionalDouble.empty())) {
            if(shouldOverrideFog()){
                renderPass.setPipeline(MyGlobalRenderPipelines.ISOLATED_SKY_TRANSLUCENT);
            }else {
                renderPass.setPipeline(MyGlobalRenderPipelines.ISOLATED_SKY);
            }
            RenderSystem.bindDefaultUniforms(renderPass);
            if(shouldOverrideFog()){
                renderPass.setUniform("Fog", fogBuffer.currentBuffer().slice(0L, FogRenderer.FOG_UBO_SIZE));
            }
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.setVertexBuffer(0, ((SkyRendererAccessor) skyRenderer).getTopSkyBuffer());
            renderPass.draw(0, 10);
        }
    }

    @Override
    public boolean renderSky(LevelRenderState levelRenderState, SkyRenderState state, Matrix4fc modelViewMatrix, Runnable setupFog) {
        setupFog.run();
        LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;
        var skyRenderer = ((LevelRendererAccessor) levelRenderer).getSkyRenderer();
        PoseStack poseStack = new PoseStack();
        FogData fogData = levelRenderState.cameraRenderState.fogData;
        try (GpuBuffer.MappedView view = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.fogBuffer.currentBuffer(), false, true)) {
            Std140Builder.intoBuffer(view.data())
                    .putVec4(new Vector4f(1, 1, 1, 0).mul(fogData.color))
                    .putFloat(fogData.environmentalStart)
                    .putFloat(fogData.environmentalEnd)
                    .putFloat(fogData.renderDistanceStart)
                    .putFloat(fogData.renderDistanceEnd)
                    .putFloat(fogData.skyEnd)
                    .putFloat(fogData.cloudEnd);
        }
        Integer firstCustomSkyTick = levelRenderState.getRenderData(LevelSpecialPerformanceHandler.FIRST_CUSTOM_SKY_TICK);
        Integer lastCustomSkyTick = levelRenderState.getRenderData(LevelSpecialPerformanceHandler.LAST_CUSTOM_SKY_TICK);
        if (firstCustomSkyTick == null && lastCustomSkyTick == null) {
            fogBuffer.rotate();
            return false;
        }
        if (firstCustomSkyTick != null) {
            var t1 = levelRenderer.getTicks() - firstCustomSkyTick;
            float alpha = Mth.clamp(t1 / 40f, 0, 1);
            float alpha1 = Mth.clamp((t1 - 20) / 20f, 0, 1);
            int skyColor = ARGB.linearLerp(alpha, state.skyColor, ARGB.color(255, 0));
            this.renderSkyDisc(skyColor);
            if (t1 < 40) {
                skyRenderer.renderSunriseAndSunset(poseStack, state.sunAngle, ARGB.linearLerp(alpha, state.sunriseAndSunsetColor, 0));
                var rainBrightness = Mth.clampedLerp(t1 / 40f, state.rainBrightness, 0);
                var starBrightness = Mth.clampedLerp(t1 / 40f, state.starBrightness, 0);
                skyRenderer.renderSunMoonAndStars(
                        poseStack, state.sunAngle, state.moonAngle, state.starAngle, state.moonPhase, rainBrightness, starBrightness
                );
            }
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(30));
            renderMainEye(alpha, poseStack);
            poseStack.popPose();
            if(alpha1 > 0){
                poseStack.pushPose();
                renderSmallEyes(alpha1, poseStack);
                poseStack.popPose();
            }
        } else {
            var t1 = levelRenderer.getTicks() - lastCustomSkyTick;
            float alpha = Mth.clamp(t1 / 40f, 0, 1);
            float alpha1 = Mth.clamp(t1 / 20f, 0, 1);
            int skyColor = ARGB.linearLerp(alpha, ARGB.color(255, 0), state.skyColor);
            this.renderSkyDisc(skyColor);
            skyRenderer.renderSunriseAndSunset(poseStack, state.sunAngle, ARGB.linearLerp(alpha, 0, state.sunriseAndSunsetColor));
            var rainBrightness = Mth.clampedLerp(t1 / 40f, 0, state.rainBrightness);
            var starBrightness = Mth.clampedLerp(t1 / 40f, 0, state.starBrightness);
            skyRenderer.renderSunMoonAndStars(
                    poseStack, state.sunAngle, state.moonAngle, state.starAngle, state.moonPhase, rainBrightness, starBrightness
            );
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(30));
            renderMainEye(1 - alpha, poseStack);
            poseStack.popPose();
            if((1-alpha1) > 0){
                poseStack.pushPose();
                renderSmallEyes(1-alpha1, poseStack);
                poseStack.popPose();
            }
        }
        if (state.shouldRenderDarkDisc) {
            skyRenderer.renderDarkDisc();
        }
        fogBuffer.rotate();
        return true;
    }


    private void renderMainEye(float alpha, PoseStack poseStack) {
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.mul(poseStack.last().pose());
        modelViewStack.translate(0.0F, 100.0F, 0.0F);
        modelViewStack.scale(30.0F, 1.0F, 30.0F);
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(modelViewStack, new Vector4f(1.0F, 1.0F, 1.0F, alpha), new Vector3f(), new Matrix4f());
        GpuTextureView color = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        GpuTextureView depth = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
        GpuBuffer indexBuffer = this.quadIndices.getBuffer(6);

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "HeiTide Sky main eye", color, OptionalInt.empty(), depth, OptionalDouble.empty())) {
            renderPass.setPipeline(EYES);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.bindTexture("Sampler0", this.celestialsAtlas.getTextureView(), this.celestialsAtlas.getSampler());
            renderPass.setVertexBuffer(0, this.mainEyeBuffer);
            renderPass.setIndexBuffer(indexBuffer, this.quadIndices.type());
            renderPass.drawIndexed(0, 0, 6, 1);
        }

        modelViewStack.popMatrix();
    }

    private void renderSmallEyes(float alpha, PoseStack poseStack){
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.mul(poseStack.last().pose());
        GpuTextureView color = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        GpuTextureView depth = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
        GpuBuffer indexBuffer = this.quadIndices.getBuffer(this.starIndexCount);
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(modelViewStack, new Vector4f(1.0F, 1.0F, 1.0F, alpha), new Vector3f(), new Matrix4f());
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "Small Eyes", color, OptionalInt.empty(), depth, OptionalDouble.empty())) {
            renderPass.setPipeline(EYES);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.bindTexture("Sampler0", this.celestialsAtlas.getTextureView(), this.celestialsAtlas.getSampler());
            renderPass.setVertexBuffer(0, this.smallEyesBuffer);
            renderPass.setIndexBuffer(indexBuffer, this.quadIndices.type());
            renderPass.drawIndexed(0, 0, this.starIndexCount, 1);
        }

        modelViewStack.popMatrix();
    }
}
