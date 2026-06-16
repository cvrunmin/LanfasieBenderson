package io.github.cvrunmin.lanfasie.benderson.content.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockParticleDustEmitterOption implements ParticleOptions {
    private static final Codec<BlockState> BLOCK_STATE_CODEC = Codec.withAlternative(
            BlockState.CODEC, BuiltInRegistries.BLOCK.byNameCodec(), Block::defaultBlockState
    );
    private final ParticleType<BlockParticleDustEmitterOption> type;
    private final BlockState state;

    private final @Nullable BlockPos pos;
    private final float radius;
    private final float speed;
    private final int life;


    public static MapCodec<BlockParticleDustEmitterOption> codec(ParticleType<BlockParticleDustEmitterOption> type) {
        return RecordCodecBuilder.mapCodec(i -> i.group(
                BLOCK_STATE_CODEC.fieldOf("block_state").forGetter(BlockParticleDustEmitterOption::getState),
                Codec.FLOAT.fieldOf("radius").validate(v -> v > 0 ? DataResult.success(v) : DataResult.error(() -> "radius must be greater than zero")).forGetter(BlockParticleDustEmitterOption::getRadius),
                Codec.FLOAT.fieldOf("speed").validate(v -> v > 0 ? DataResult.success(v) : DataResult.error(() -> "speed must be greater than zero")).forGetter(BlockParticleDustEmitterOption::getSpeed),
                Codec.INT.fieldOf("life").validate(v -> v >= 0 ? DataResult.success(v) : DataResult.error(() -> "life must be no less than zero")).forGetter(BlockParticleDustEmitterOption::getLife)
        ).apply(i, ((state, radius, speed, life) -> new BlockParticleDustEmitterOption(type, state, radius, speed, life))));
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, BlockParticleDustEmitterOption> streamCodec(ParticleType<BlockParticleDustEmitterOption> type) {
        return StreamCodec.composite(
                ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY),
                option -> option.state,
                net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs.connectionAware(
                        ByteBufCodecs.optional(net.minecraft.core.BlockPos.STREAM_CODEC),
                        net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs.uncheckedUnit(java.util.Optional.empty())
                ),
                option -> java.util.Optional.ofNullable(option.pos),
                ByteBufCodecs.FLOAT,
                option -> option.radius,
                ByteBufCodecs.FLOAT,
                option -> option.speed,
                ByteBufCodecs.INT,
                option -> option.life,
                (state, pos, radius, speed, life) -> new BlockParticleDustEmitterOption(type, state, pos.orElse(null), radius, speed, life)
        );
    }

    public BlockParticleDustEmitterOption(ParticleType<BlockParticleDustEmitterOption> type, BlockState state, float radius, float speed, int life) {
        this(type, state, null, radius, speed, life);
    }

    public BlockParticleDustEmitterOption(ParticleType<BlockParticleDustEmitterOption> type, BlockState state, @Nullable BlockPos pos, float radius, float speed, int life) {
        this.type = type;
        this.state = state;
        this.pos = pos;
        this.radius = radius;
        this.speed = speed;
        this.life = life;
    }


    @Override
    public ParticleType<?> getType() {
        return type;
    }

    public BlockState getState() {
        return state;
    }

    @Nullable
    public BlockPos getPos() {
        return pos;
    }

    public float getRadius() {
        return radius;
    }

    public float getSpeed() {
        return speed;
    }

    public int getLife() {
        return life;
    }
}
