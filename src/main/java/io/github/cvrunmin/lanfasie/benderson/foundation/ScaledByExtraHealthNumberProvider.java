package io.github.cvrunmin.lanfasie.benderson.foundation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Set;

public record ScaledByExtraHealthNumberProvider(NumberProvider base, MultiplierType multiplierType) implements NumberProvider {
    public enum MultiplierType implements StringRepresentable {
        LINEAR("linear"), LINEAR_ONE_TENTH("linear_one_tenth"), LOG10("log10");

        public static final Codec<MultiplierType> CODEC = StringRepresentable.fromEnum(MultiplierType::values);
        private final String name;

        MultiplierType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final MapCodec<ScaledByExtraHealthNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(i ->
            i.group(NumberProviders.CODEC.fieldOf("base").forGetter(ScaledByExtraHealthNumberProvider::base),
                    MultiplierType.CODEC.optionalFieldOf("multiplierType", MultiplierType.LINEAR_ONE_TENTH).forGetter(ScaledByExtraHealthNumberProvider::multiplierType)
                            )
                    .apply(i, ScaledByExtraHealthNumberProvider::new));

    @Override
    public float getFloat(LootContext context) {
        var baseValue = base.getFloat(context);
        var param = context.getParameter(LootContextParams.THIS_ENTITY);
        if(param instanceof LivingEntity livingEntity){
            var multiplier = livingEntity.getAttributeValue(Attributes.MAX_HEALTH) / livingEntity.getAttributeBaseValue(Attributes.MAX_HEALTH);
            switch (multiplierType) {
                case LINEAR_ONE_TENTH -> multiplier = multiplier * 0.1;
                case LOG10 -> multiplier = Math.log10(multiplier);
                case null, default -> {
                }
            }
            return (float) (baseValue * multiplier);
        }
        return baseValue;
    }

    @Override
    public MapCodec<? extends NumberProvider> codec() {
        return MAP_CODEC;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.THIS_ENTITY);
    }

    @Override
    public void validate(ValidationContext context) {
        NumberProvider.super.validate(context);
        Validatable.validate(context, "base", base);
    }
    public static ScaledByExtraHealthNumberProvider forExtreme(NumberProvider base){
        return new ScaledByExtraHealthNumberProvider(base, MultiplierType.LINEAR_ONE_TENTH);
    }
}
