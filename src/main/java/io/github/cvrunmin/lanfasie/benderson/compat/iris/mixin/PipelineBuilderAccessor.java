package io.github.cvrunmin.lanfasie.benderson.compat.iris.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(RenderPipeline.Builder.class)
public interface PipelineBuilderAccessor {
    @Accessor("location")
    Optional<Identifier> getLocation();
}
