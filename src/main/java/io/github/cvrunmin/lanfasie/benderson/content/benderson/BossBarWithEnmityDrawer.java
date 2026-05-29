package io.github.cvrunmin.lanfasie.benderson.content.benderson;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;

@EventBusSubscriber(modid = LanfasieBenderson.MODID, value = Dist.CLIENT)
public class BossBarWithEnmityDrawer {
    @SubscribeEvent
    public static void onCustomizeBossEventProgress(CustomizeGuiOverlayEvent.BossEventProgress event){
        Minecraft mc = Minecraft.getInstance();
        if(mc.level != null){
            var level = mc.level;
            var maybeEntity = level.getEntity(event.getBossEvent().getId());
            if(!(maybeEntity instanceof Benderson boss)) return;
            var enmityList = boss.enmityList;
            if(enmityList == null) return;
            var player = mc.player;
            if(player == null) return;
            if(!enmityList.containsKey(player.getUUID())) return;
            var barInfo = boss.getEnmityBarInfo(player.getUUID());
            if(barInfo.rank() == -1) return;
            var targeted = boss.isPlayerTargeted(player.getUUID());
            Component enmityText;
            if(targeted){
                enmityText = Component.translatable("text.lanfasie_benderson.enmity_bar.aggro");
            }else{
                enmityText = Component.literal(String.valueOf(barInfo.rank()));
            }
            event.getGuiGraphics().text(mc.font, enmityText, event.getX() + 20, event.getY() + 5, 0xffffffff);
            event.getGuiGraphics().fill(event.getX(), event.getY() + 6, event.getX() + 18, event.getY() + 11, 0xffffffff);
            event.getGuiGraphics().fill((int) (event.getX() + 1 + (16 * (barInfo.barPercentage()))), event.getY() + 7, event.getX() + 17, event.getY() + 10, 0xff000000);

        }
    }
}
