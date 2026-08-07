package io.github.cvrunmin.lanfasie.benderson.foundation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BossSummonInfo(int arenaRadius, double damageGate) {
    public static final BossSummonInfo DEFAULT = new BossSummonInfo(24, 0.01);
    public static final Codec<BossSummonInfo> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                    Codec.INT.optionalFieldOf("arenaRadius", DEFAULT.arenaRadius).validate(val -> val >= 1 && val <= 32 ? DataResult.success(val) : DataResult.error(() -> "arenaRadius must be between 1 and 32", BossSummonInfo.DEFAULT.arenaRadius)).forGetter(BossSummonInfo::arenaRadius),
                    Codec.DOUBLE.optionalFieldOf("damageGate", DEFAULT.damageGate).validate(val -> val > 0 && val <= 1 ? DataResult.success(val) : DataResult.error(() -> "damageGate must be larger than 0 and no larger than 1", BossSummonInfo.DEFAULT.damageGate)).forGetter(BossSummonInfo::damageGate)
            ).apply(i, BossSummonInfo::new)
    );
    public static final StreamCodec<ByteBuf, BossSummonInfo> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            BossSummonInfo::arenaRadius,
            ByteBufCodecs.DOUBLE,
            BossSummonInfo::damageGate,
            BossSummonInfo::new
    );

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private Integer arenaRadius;
        private Double damageGate;

        public Builder(){

        }

        public Builder setArenaRadius(int value){
            if(value >= 1 && value <= 32) {
                arenaRadius = value;
            }
            return this;
        }

        public Builder setDamageGate(double value){
            if(0 < value && value <= 1) {
                this.damageGate = value;
            }
            return this;
        }

        public BossSummonInfo build(){
            var realArenaRadius = arenaRadius != null ? arenaRadius : DEFAULT.arenaRadius;
            var realDamageGate = damageGate != null ? damageGate : DEFAULT.damageGate;
            return new BossSummonInfo(realArenaRadius, realDamageGate);
        }
    }
}
