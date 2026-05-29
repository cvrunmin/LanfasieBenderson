package io.github.cvrunmin.lanfasie.benderson.data;

import com.mojang.serialization.MapCodec;
import io.github.cvrunmin.lanfasie.benderson.index.AllMobEffects;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record IsProvokingModelProperty() implements ConditionalItemModelProperty {

    public static final MapCodec<IsProvokingModelProperty> MAP_CODEC =  MapCodec.unit(new IsProvokingModelProperty());

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }

    @Override
    public boolean get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        return owner != null && owner.hasEffect(AllMobEffects.AGGRO_UP);
    }
}
