package io.github.cvrunmin.lanfasie.benderson.index;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.content.particles.BlockParticleDustEmitterOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class AllParticleTypes {
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPE = DeferredRegister.create(Registries.PARTICLE_TYPE, LanfasieBenderson.MODID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<BlockParticleDustEmitterOption>> DUST_SUCKING = PARTICLE_TYPE.register("dust_sucking", () -> createType(true, BlockParticleDustEmitterOption::codec, BlockParticleDustEmitterOption::streamCodec));
    public static final DeferredHolder<ParticleType<?>, ParticleType<BlockParticleDustEmitterOption>> DUST_BLOWING = PARTICLE_TYPE.register("dust_blowing", () -> createType(true, BlockParticleDustEmitterOption::codec, BlockParticleDustEmitterOption::streamCodec));

    public static void register(IEventBus modBus){
        PARTICLE_TYPE.register(modBus);
    }

    private static <T extends ParticleOptions> ParticleType<T> createType(boolean overrideLimiter, Function<ParticleType<T>, MapCodec<T>> codecFunc, Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecFunc){
        return new ParticleType<>(overrideLimiter) {
            @Override
            public MapCodec<T> codec() {
                return codecFunc.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodecFunc.apply(this);
            }
        };
    }
}
