package io.github.cvrunmin.lanfasie.benderson.content.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class DustSuckingBlowingParticleEmitter extends NoRenderParticle {
    private final BlockState state;
    private final @Nullable BlockPos pos;
    private final float radius;
    private final float speed;
    private final boolean isBlowing;
    private final ParticleEngine engine;

    public DustSuckingBlowingParticleEmitter(ClientLevel level, BlockParticleDustEmitterOption option, boolean isBlowing, double x, double y, double z) {
        super(level, x, y, z);
        this.engine = Minecraft.getInstance().particleEngine;
        this.state = option.getState();
        this.pos = option.getPos();
        this.radius = option.getRadius();
        this.speed = option.getSpeed();
        this.lifetime = option.getLife();
        this.isBlowing = isBlowing;
    }

    public DustSuckingBlowingParticleEmitter(ClientLevel level, BlockParticleDustEmitterOption option, boolean isBlowing, double x, double y, double z, double xa, double ya, double za) {
        super(level, x, y, z, xa, ya, za);
        this.engine = Minecraft.getInstance().particleEngine;
        this.state = option.getState();
        this.pos = option.getPos();
        this.radius = option.getRadius();
        this.speed = option.getSpeed();
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
            var particle = new CustomTerrainParticle(level, x, y, z, 0, 0, 0, state);
            particle.setGravity(0);
            particle.updateSprite(state, pos);
            particle.setLifetime((int) (radius / speed));
            if(isBlowing){
                particle.setParticleSpeed(xd * speed, 0, zd * speed);
            }else{
                particle.setParticleSpeed(-xd * speed, 0, -zd * speed);
                particle.setGravity(0);
                particle.setFriction(1);
                particle.setHasPhysics(false);
            }
            engine.add(particle);
        }
        age++;
        if(age >= lifetime){
            remove();
        }
    }

    public static class SuckingProvider implements ParticleProvider<BlockParticleDustEmitterOption>{

        @Override
        public @Nullable Particle createParticle(BlockParticleDustEmitterOption options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new DustSuckingBlowingParticleEmitter(level, options, false, x, y, z);
        }
    }

    public static class BlowingProvider implements ParticleProvider<BlockParticleDustEmitterOption>{

        @Override
        public @Nullable Particle createParticle(BlockParticleDustEmitterOption options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new DustSuckingBlowingParticleEmitter(level, options, true, x, y, z);
        }
    }

    static class CustomTerrainParticle extends TerrainParticle{

        public CustomTerrainParticle(ClientLevel level, double x, double y, double z, double xa, double ya, double za, BlockState blockState) {
            super(level, x, y, z, xa, ya, za, blockState);
        }

        public CustomTerrainParticle(ClientLevel level, double x, double y, double z, double xa, double ya, double za, BlockState blockState, BlockPos pos) {
            super(level, x, y, z, xa, ya, za, blockState, pos);
        }

        public void setGravity(float gravity){
            this.gravity = gravity;
        }

        public void setFriction(float v){
            this.friction = v;
        }

        public void setHasPhysics(boolean flag){
            this.hasPhysics = flag;
        }
    }
}
