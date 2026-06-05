package io.github.cvrunmin.lanfasie.benderson;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.SelectMusicEvent;

import java.util.Objects;

@EventBusSubscriber(modid = LanfasieBenderson.MODID, value = Dist.CLIENT)
public class BossThemeMusicManager {
    private static BossMusicTrackPlayer bendersonTheme1Player = new BossMusicTrackPlayer(AllSoundEvents.BENDERSON_BOSS_THEME_1, 169.84f, 2.0f, 162.46f);

    @SubscribeEvent
    public static void postClientTick(ClientTickEvent.Post event){
        var mc = Minecraft.getInstance();
        var shouldLoadBossMusic = false;
        if(mc.level != null && mc.player != null){
            var level = mc.level;
            var player = mc.player;
            var entities = level.getEntitiesOfClass(Benderson.class, AABB.ofSize(player.position(), 64, 20, 64));
            for (Benderson benderson : entities) {
                var bossArena = AABB.ofSize(benderson.clientGetCombatArenaCenter(), benderson.getArenaRadius() * 2, 14, benderson.getArenaRadius() * 2);
                if(bossArena.contains(player.position())){
                    if(!bendersonTheme1Player.isActive() || bendersonTheme1Player.isMarkedDeactivate()) {
                        bendersonTheme1Player.startPlaying();
                    }
                    shouldLoadBossMusic = true;
                    break;
                }
            }
        }
        if(!shouldLoadBossMusic){
            bendersonTheme1Player.deactivate();
        }
        bendersonTheme1Player.clientTick();
    }

    public static boolean anyBossThemeActive(){
        if(bendersonTheme1Player.isActive()) return true;
        return false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onSelectMusic(SelectMusicEvent event){
        if(BossThemeMusicManager.anyBossThemeActive()) event.overrideMusic(null);
    }
}
