package io.github.cvrunmin.lanfasie.benderson.index;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.LevelSpecialPerformanceHandler;
import io.github.cvrunmin.lanfasie.benderson.mixin.LevelRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.CustomSkyboxRenderer;
import net.neoforged.neoforge.client.event.RegisterCustomEnvironmentEffectRendererEvent;
import org.joml.Matrix4fc;

@EventBusSubscriber(modid = LanfasieBenderson.MODID, value = Dist.CLIENT)
public class AllCustomEnvRenderer {
    public static final Identifier HEI_TIDE_SKY_RENDERER = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "hei_tide_sky");

    @SubscribeEvent
    public static void registerCustomEnvironmentRenderer(RegisterCustomEnvironmentEffectRendererEvent event){
        event.registerSkyboxRenderer(HEI_TIDE_SKY_RENDERER, new CustomSkyboxRenderer() {
            @Override
            public boolean renderSky(LevelRenderState levelRenderState, SkyRenderState state, Matrix4fc modelViewMatrix, Runnable setupFog) {
                setupFog.run();
                LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;
                var skyRenderer = ((LevelRendererAccessor) levelRenderer).getSkyRenderer();
                PoseStack poseStack = new PoseStack();
                Integer firstCustomSkyTick = levelRenderState.getRenderData(LevelSpecialPerformanceHandler.FIRST_CUSTOM_SKY_TICK);
                Integer lastCustomSkyTick = levelRenderState.getRenderData(LevelSpecialPerformanceHandler.LAST_CUSTOM_SKY_TICK);
                if(firstCustomSkyTick == null && lastCustomSkyTick == null) return false;
                if(firstCustomSkyTick != null){
                    var t1 = levelRenderer.getTicks() - firstCustomSkyTick;
                    skyRenderer.renderSkyDisc(ARGB.linearLerp(Mth.clamp(t1 / 40f, 0, 1), state.skyColor, ARGB.color(255, 0)));
                    if(t1 < 40){
                        skyRenderer.renderSunriseAndSunset(poseStack, state.sunAngle, ARGB.linearLerp(Mth.clamp(t1 / 40f, 0, 1), state.sunriseAndSunsetColor, 0));
                        var rainBrightness = Mth.clampedLerp(t1 / 40f, state.rainBrightness, 0);
                        var starBrightness = Mth.clampedLerp(t1 / 40f, state.starBrightness, 0);
                        skyRenderer.renderSunMoonAndStars(
                                poseStack, state.sunAngle, state.moonAngle, state.starAngle, state.moonPhase, rainBrightness, starBrightness
                        );
                    }
                }
                else {
                    var t1 = levelRenderer.getTicks() - lastCustomSkyTick;
                    skyRenderer.renderSkyDisc(ARGB.linearLerp(Mth.clamp(t1 / 40f, 0, 1), ARGB.color(255, 0), state.skyColor));
                    skyRenderer.renderSunriseAndSunset(poseStack, state.sunAngle, ARGB.linearLerp(Mth.clamp(t1 / 40f, 0, 1), 0, state.sunriseAndSunsetColor));
                    var rainBrightness = Mth.clampedLerp(t1 / 40f, 0, state.rainBrightness);
                    var starBrightness = Mth.clampedLerp(t1 / 40f, 0, state.starBrightness);
                    skyRenderer.renderSunMoonAndStars(
                            poseStack, state.sunAngle, state.moonAngle, state.starAngle, state.moonPhase, rainBrightness, starBrightness
                    );
                }
                if (state.shouldRenderDarkDisc) {
                    skyRenderer.renderDarkDisc();
                }
                return true;
            }
        });
    }
}
