package io.github.cvrunmin.lanfasie.benderson;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllCustomEnvRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.CustomEnvironmentEffectsRendererManager;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = LanfasieBenderson.MODID)
public class LevelSpecialPerformanceHandler {
    public static ContextKey<Integer> FIRST_CUSTOM_SKY_TICK = new ContextKey<>(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "first_custom_sky_tick"));
    public static ContextKey<Integer> LAST_CUSTOM_SKY_TICK = new ContextKey<>(Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "last_custom_sky_tick"));
    private static boolean wasRenderingCustomSky = false;
    private static Integer firstRenderCustomSkyTick = null;
    private static Integer lastRenderCustomSkyTick = null;

    @SubscribeEvent
    public static void onExtractLevelRenderState(ExtractLevelRenderStateEvent event){
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) return;
        var entities = event.getLevel().getEntitiesOfClass(Benderson.class, AABB.ofSize(player.position(), 64, 20, 64));
        var shouldRenderSpecialSky = false;
        for (Benderson benderson : entities) {
            if(benderson.isNoAi()) continue;
            var bossArena = AABB.ofSize(benderson.getCombatArenaCenterVec3(), benderson.getArenaRadius() * 2, 20, benderson.getArenaRadius() * 2);
            if(bossArena.contains(player.position()) &&
                    (benderson.getBodyState() == Benderson.BodyState.TRANSITION_UNFORGIVEN_POST || benderson.getBodyState() == Benderson.BodyState.UNFORGIVEN)){
                shouldRenderSpecialSky = true;
                break;
            }
        }
        if(shouldRenderSpecialSky){
            if(!wasRenderingCustomSky || firstRenderCustomSkyTick == null){
                if(lastRenderCustomSkyTick != null && event.getRenderTick() - lastRenderCustomSkyTick < 40){
                    firstRenderCustomSkyTick = event.getRenderTick() - (40 - event.getRenderTick() + lastRenderCustomSkyTick);
                }else {
                    firstRenderCustomSkyTick = event.getRenderTick();
                }
                wasRenderingCustomSky = true;
                lastRenderCustomSkyTick = null;
            }
            event.getRenderState().setRenderData(FIRST_CUSTOM_SKY_TICK, firstRenderCustomSkyTick);
            event.getRenderState().cloudColor = ARGB.multiplyAlpha(event.getRenderState().cloudColor, Mth.clamp(1 - (event.getRenderTick() - firstRenderCustomSkyTick + event.getDeltaTracker().getGameTimeDeltaPartialTick(false)) / 40f, 0, 1));
            event.getRenderState().customSkyboxRenderer = CustomEnvironmentEffectsRendererManager.getCustomSkyboxRenderer(AllCustomEnvRenderer.HEI_TIDE_SKY_RENDERER);
        }else{
            if(wasRenderingCustomSky){
                if(lastRenderCustomSkyTick == null){
                    if(event.getRenderTick() - firstRenderCustomSkyTick < 40){
                        lastRenderCustomSkyTick = event.getRenderTick() - (40 - event.getRenderTick() + firstRenderCustomSkyTick);
                    }else{
                        lastRenderCustomSkyTick = event.getRenderTick();
                    }
                    firstRenderCustomSkyTick = null;
                }else if(event.getRenderTick() - lastRenderCustomSkyTick > 40){
                    wasRenderingCustomSky = false;
                    lastRenderCustomSkyTick = null;
                    return;
                }
                event.getRenderState().setRenderData(LAST_CUSTOM_SKY_TICK, lastRenderCustomSkyTick);
                event.getRenderState().cloudColor = ARGB.multiplyAlpha(event.getRenderState().cloudColor, Mth.clamp((event.getRenderTick() - lastRenderCustomSkyTick + event.getDeltaTracker().getGameTimeDeltaPartialTick(false)) / 40f, 0, 1));
                event.getRenderState().customSkyboxRenderer = CustomEnvironmentEffectsRendererManager.getCustomSkyboxRenderer(AllCustomEnvRenderer.HEI_TIDE_SKY_RENDERER);
            }
        }
    }
}
