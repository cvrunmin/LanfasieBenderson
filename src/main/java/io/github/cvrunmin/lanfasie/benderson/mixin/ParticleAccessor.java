package io.github.cvrunmin.lanfasie.benderson.mixin;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class)
public interface ParticleAccessor {

    @Accessor("gravity")
    void setGravity(float gravity);

    @Accessor("friction")
    void setFriction(float v);

    @Accessor("hasPhysics")
    void setHasPhysics(boolean flag);
}
