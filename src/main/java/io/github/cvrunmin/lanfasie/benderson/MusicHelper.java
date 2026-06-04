package io.github.cvrunmin.lanfasie.benderson;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.Music;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.SelectMusicEvent;

import java.util.Objects;

@EventBusSubscriber(modid = LanfasieBenderson.MODID)
public class MusicHelper {
    public static final Music BENDERSON_BOSS_THEME_1 = new Music(AllSoundEvents.BENDERSON_BOSS_THEME_1, 0, 0, true);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onSelectMusic(SelectMusicEvent event){
        var mc = Minecraft.getInstance();
        var shouldLoadBossMusic = false;
        if(mc.level != null && mc.player != null){
            var level = mc.level;
            var player = mc.player;
            var entities = level.getEntitiesOfClass(Benderson.class, AABB.ofSize(player.position(), 64, 20, 64));
            for (Benderson benderson : entities) {
                var bossArena = AABB.ofSize(benderson.clientGetCombatArenaCenter(), benderson.getArenaRadius() * 2, 14, benderson.getArenaRadius() * 2);
                if(bossArena.contains(player.position())){
                    event.overrideMusic(BENDERSON_BOSS_THEME_1);
                    shouldLoadBossMusic = true;
                    break;
                }
            }
        }
        if (event.getPlayingMusic() != null && !shouldLoadBossMusic) {
            if (Objects.equals(event.getPlayingMusic().getIdentifier(), AllSoundEvents.BENDERSON_BOSS_THEME_1.getId())) {
                event.setMusic(null);
            }
        }
    }
}
