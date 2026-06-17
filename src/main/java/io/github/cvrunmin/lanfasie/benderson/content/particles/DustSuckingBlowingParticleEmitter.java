package io.github.cvrunmin.lanfasie.benderson.content.particles;

import io.github.cvrunmin.lanfasie.benderson.mixin.ParticleAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;

public class DustSuckingBlowingParticleEmitter extends NoRenderParticle {
    private final int color;
    private final float scale;
    private final float radius;
    private final float speed;
    private final float particleGravity;
    private final boolean isBlowing;
    private final ParticleEngine engine;

    public DustSuckingBlowingParticleEmitter(ClientLevel level, ColoredDustEmitterOption option, boolean isBlowing, double x, double y, double z) {
        super(level, x, y, z);
        this.engine = Minecraft.getInstance().particleEngine;
        this.color = option.getColor();
        this.scale = option.getScale();
        this.radius = option.getRadius();
        this.speed = option.getSpeed();
        this.particleGravity = option.getGravity();
        this.lifetime = option.getLife();
        this.isBlowing = isBlowing;
    }

    public DustSuckingBlowingParticleEmitter(ClientLevel level, ColoredDustEmitterOption option, boolean isBlowing, double x, double y, double z, double xa, double ya, double za) {
        super(level, x, y, z, xa, ya, za);
        this.engine = Minecraft.getInstance().particleEngine;
        this.color = option.getColor();
        this.scale = option.getScale();
        this.radius = option.getRadius();
        this.speed = option.getSpeed();
        this.particleGravity = option.getGravity();
        this.lifetime = option.getLife();
        this.isBlowing = isBlowing;
    }

    @Override
    public void tick() {
        var numParticles = 4 * ((int) Math.max(1, Math.floor(Math.PI * radius / 2)));
        for (int i = 0; i < numParticles; i++) {
            var theta = Math.PI * 2 * i / numParticles;
            var xd = Math.cos(theta);
            var zd = Math.sin(theta);
            double x = this.x, y = this.y, z = this.z;
            if(!isBlowing){
                x += xd * radius;
                z += zd * radius;
            }
            var particle = engine.createParticle(new DustParticleOptions(color, scale), x, y, z, 0, 0, 0);
            if(particle != null){
                ((ParticleAccessor) particle).setGravity(this.particleGravity);
                particle.setLifetime((int) (radius / speed));
                if(isBlowing){
                    particle.setParticleSpeed(xd * speed, 0, zd * speed);
                }else{
                    particle.setParticleSpeed(-xd * speed, 0, -zd * speed);
                    ((ParticleAccessor) particle).setGravity(this.particleGravity);
                    ((ParticleAccessor) particle).setFriction(1);
                    ((ParticleAccessor) particle).setHasPhysics(false);
                }
            }
        }
        age++;
        if(age >= lifetime){
            remove();
        }
    }

    public static class SuckingProvider implements ParticleProvider<ColoredDustEmitterOption> {

        @Override
        public @Nullable Particle createParticle(ColoredDustEmitterOption options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new DustSuckingBlowingParticleEmitter(level, options, false, x, y, z);
        }
    }

    public static class BlowingProvider implements ParticleProvider<ColoredDustEmitterOption>{

        @Override
        public @Nullable Particle createParticle(ColoredDustEmitterOption options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new DustSuckingBlowingParticleEmitter(level, options, true, x, y, z);
        }
    }
}
