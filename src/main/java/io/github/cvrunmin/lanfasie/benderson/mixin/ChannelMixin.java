package io.github.cvrunmin.lanfasie.benderson.mixin;

import com.mojang.blaze3d.audio.Channel;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import io.github.cvrunmin.lanfasie.benderson.foundation.ISoundPositionVisitor;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Channel.class)
public class ChannelMixin implements ISoundPositionVisitor {
    @Shadow
    @Final
    private int source;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Override
    public void setSoundPositionSec(float sec) {
        AL10.alSourcef(source, AL11.AL_SEC_OFFSET, sec);
        int err = AL10.alGetError();
        if(err != AL10.AL_NO_ERROR){
            var errStr = switch (err){
                case AL10.AL_INVALID_ENUM -> "invalid enum";
                case AL10.AL_INVALID_NAME -> "invalid name";
                case AL10.AL_INVALID_VALUE -> "invalid value";
                case AL10.AL_INVALID_OPERATION -> "invalid operation";
                default -> "unknown error";
            };
            LOGGER.error("{}: {}", "set sound offset", errStr);
        }
    }

    @Override
    public float getSoundPositionSec() {
        float secOffset = AL10.alGetSourcef(source, AL11.AL_SEC_OFFSET);
        int err = AL10.alGetError();
        if(err != AL10.AL_NO_ERROR){
            var errStr = switch (err){
                case AL10.AL_INVALID_ENUM -> "invalid enum";
                case AL10.AL_INVALID_NAME -> "invalid name";
                case AL10.AL_INVALID_VALUE -> "invalid value";
                case AL10.AL_INVALID_OPERATION -> "invalid operation";
                default -> "unknown error";
            };
            LOGGER.error("{}: {}", "set sound offset", errStr);
        }
        return secOffset;
    }
}
