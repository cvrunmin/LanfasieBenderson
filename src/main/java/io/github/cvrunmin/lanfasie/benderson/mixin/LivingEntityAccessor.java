package io.github.cvrunmin.lanfasie.benderson.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("lastDamageSource")
    void setLastDamageSource(DamageSource source);

    @Accessor("lastDamageSource")
    DamageSource getLastDamageSource();

    @Accessor("lastDamageStamp")
    void setLastDamageStamp(long value);

    @Accessor("lastDamageStamp")
    long getLastDamageStamp();
}
