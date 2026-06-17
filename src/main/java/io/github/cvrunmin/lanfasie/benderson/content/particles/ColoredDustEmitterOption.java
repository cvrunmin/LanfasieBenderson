package io.github.cvrunmin.lanfasie.benderson.content.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public class ColoredDustEmitterOption extends ScalableParticleOptionsBase {
    private final ParticleType<ColoredDustEmitterOption> type;
    private final int color;
    private final float radius;
    private final float speed;
    private final float gravity;
    private final int life;

    public static MapCodec<ColoredDustEmitterOption> codec(ParticleType<ColoredDustEmitterOption> type){
        return RecordCodecBuilder.mapCodec(
                i -> i.group(
                                ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter(o -> o.color),
                                SCALE.fieldOf("scale").forGetter(ScalableParticleOptionsBase::getScale),
                                Codec.FLOAT.fieldOf("radius").validate(v -> v > 0 ? DataResult.success(v) : DataResult.error(() -> "radius must be greater than zero")).forGetter(ColoredDustEmitterOption::getRadius),
                                Codec.FLOAT.fieldOf("speed").validate(v -> v > 0 ? DataResult.success(v) : DataResult.error(() -> "speed must be greater than zero")).forGetter(ColoredDustEmitterOption::getSpeed),
                                Codec.FLOAT.optionalFieldOf("gravity", 0f).forGetter(ColoredDustEmitterOption::getGravity),
                                Codec.INT.fieldOf("life").validate(v -> v >= 0 ? DataResult.success(v) : DataResult.error(() -> "life must be no less than zero")).forGetter(ColoredDustEmitterOption::getLife)
                        )
                        .apply(i, (color, scale, radius, speed, g, life) -> new ColoredDustEmitterOption(type, color, scale, radius, speed, g, life)));
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, ColoredDustEmitterOption> streamCodec(ParticleType<ColoredDustEmitterOption> type){
        return StreamCodec.composite(
                ByteBufCodecs.INT,
                o -> o.color,
                ByteBufCodecs.FLOAT,
                ScalableParticleOptionsBase::getScale,
                ByteBufCodecs.FLOAT,
                option -> option.radius,
                ByteBufCodecs.FLOAT,
                option -> option.speed,
                ByteBufCodecs.FLOAT,
                option -> option.gravity,
                ByteBufCodecs.INT,
                option -> option.life,
                (color,  scale, radius, speed, g, life) -> new ColoredDustEmitterOption(type, color, scale, radius, speed, g, life)
        );
    }

    public ColoredDustEmitterOption(ParticleType<ColoredDustEmitterOption> type, int color, float scale, float radius, float speed, float gravity, int life) {
        super(scale);
        this.type = type;
        this.color = color;
        this.radius = radius;
        this.speed = speed;
        this.gravity = gravity;
        this.life = life;
    }

    @Override
    public ParticleType<?> getType() {
        return type;
    }

    public int getColor() {
        return color;
    }

    public float getSpeed() {
        return speed;
    }

    public float getRadius() {
        return radius;
    }

    public float getGravity() {
        return gravity;
    }

    public int getLife() {
        return life;
    }
}
