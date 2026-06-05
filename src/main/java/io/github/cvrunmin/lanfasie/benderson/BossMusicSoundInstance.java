package io.github.cvrunmin.lanfasie.benderson;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class BossMusicSoundInstance extends AbstractSoundInstance {
    private boolean interruptFlag;

    private float desiredVolume = 1.0f;

    protected BossMusicSoundInstance(SoundEvent event, SoundSource source, RandomSource random) {
        super(event, source, random);
        this.attenuation = Attenuation.NONE;
        this.delay = 0;
        this.x = 0;
        this.y = 0;
        this.z = 0;
        relative = true;
        looping = false;
    }

    public static BossMusicSoundInstance fromMusic(SoundEvent music){
        return new BossMusicSoundInstance(music, SoundSource.MUSIC, SoundInstance.createUnseededRandom());
    }

    public void markForInterrupt(){
        interruptFlag = true;
    }

    public boolean isInterrupted() {
        return interruptFlag;
    }

    public void setVolume(float v) {
        this.volume = v;
        this.desiredVolume = v;
    }

    public void fadeToVolume(float v){
        desiredVolume = Mth.clamp(v, 0, 1);
    }

    public void fadeVolume(){
        if(Math.abs(this.desiredVolume - volume) <= 1e-4) return;
        if(volume < this.desiredVolume){
            volume = volume + Mth.clamp(volume, 5e-4f, 0.05f);
            if(volume > this.desiredVolume) volume = this.desiredVolume;
        }else {
            volume = 0.1f * desiredVolume + 0.9f * volume;
            if(Math.abs(this.desiredVolume - volume) <= 1e-2 || volume < desiredVolume){
                volume = desiredVolume;
            }
        }
        volume = Mth.clamp(volume, 0, 1);
    }

    public float getBaseVolume(){
        return volume;
    }

    public float getDesiredVolume() {
        return desiredVolume;
    }
}
