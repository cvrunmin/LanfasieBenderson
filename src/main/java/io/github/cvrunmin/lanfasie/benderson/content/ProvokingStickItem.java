package io.github.cvrunmin.lanfasie.benderson.content;

import io.github.cvrunmin.lanfasie.benderson.MobEffectRemovalProtector;
import io.github.cvrunmin.lanfasie.benderson.index.AllMobEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ProvokingStickItem extends Item {
    public ProvokingStickItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if(!level.isClientSide()){
            if(player.hasEffect(AllMobEffects.AGGRO_UP)){
                MobEffectRemovalProtector.grantAndRemove(player, AllMobEffects.AGGRO_UP);
            }else{
                player.addEffect(new MobEffectInstance(AllMobEffects.AGGRO_UP, -1, 0, false, false, true));
            }
        }
        return InteractionResult.SUCCESS;
    }
}
