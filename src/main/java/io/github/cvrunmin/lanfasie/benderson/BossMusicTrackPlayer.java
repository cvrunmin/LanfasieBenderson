package io.github.cvrunmin.lanfasie.benderson;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.cvrunmin.lanfasie.benderson.foundation.ISoundPositionVisitor;
import io.github.cvrunmin.lanfasie.benderson.mixin.ChannelHandleAccessor;
import io.github.cvrunmin.lanfasie.benderson.mixin.SoundEngineAccessor;
import io.github.cvrunmin.lanfasie.benderson.mixin.SoundManagerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.Holder;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;

import java.io.IOException;
import java.util.Optional;

public class BossMusicTrackPlayer {
    public record MusicTransitionMetadataSection(float crossFadeStartTime, float crossFadeDuration, float crossFadeTargetTime){
        public static final Codec<MusicTransitionMetadataSection> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(Codec.FLOAT.fieldOf("crossFadeStartTime").forGetter(MusicTransitionMetadataSection::crossFadeStartTime),
                        Codec.FLOAT.fieldOf("crossFadeDuration").forGetter(MusicTransitionMetadataSection::crossFadeDuration),
                        Codec.FLOAT.optionalFieldOf("crossFadeTargetTime", 0f).forGetter(MusicTransitionMetadataSection::crossFadeTargetTime)
                ).apply(instance, MusicTransitionMetadataSection::new));

        public static final MetadataSectionType<MusicTransitionMetadataSection> TYPE = new MetadataSectionType<>("lanfasie_benderson:music_transition", CODEC);
    }


    private final Holder<SoundEvent> soundEvent;
    private final float predefinedCrossFadeDuration;
    private final float predefinedCrossFadeStartTime;

    private boolean initialized;
    private float crossFadeDuration;
    private float crossFadeStartTime;
    private float crossFadeTargetTime;

    private boolean active = false;
    private boolean markedDeactivate = false;
    private BossMusicSoundInstance[] tracks = new BossMusicSoundInstance[2];
    private int activeTrack = 0;

    public BossMusicTrackPlayer(Holder<SoundEvent> soundEvent, float musicDuration, float predefinedCrossFadeDuration, float predefinedCrossFadeStartTime){
        if(predefinedCrossFadeStartTime > musicDuration){
            throw new IllegalArgumentException("cross-fade start %f > music duration %f".formatted(predefinedCrossFadeStartTime, musicDuration));
        }
        this.soundEvent = soundEvent;
        this.predefinedCrossFadeDuration = predefinedCrossFadeDuration;
        this.predefinedCrossFadeStartTime = predefinedCrossFadeStartTime;
    }

    public void startPlaying(){
        if(active && !markedDeactivate) return;
        active = true;
        if(markedDeactivate && tracks[activeTrack] != null && Minecraft.getInstance().getSoundManager().isActive(tracks[activeTrack])) {
            tracks[activeTrack].fadeToVolume(1.0f);
        }else{
            activeTrack = 0;
            tracks[0] = BossMusicSoundInstance.fromMusic(soundEvent.value());
            tracks[1] = null;
            Minecraft.getInstance().getSoundManager().play(tracks[0]);
            if(!initialized){
                var optionalMeta = Optional.ofNullable(tracks[0].getSound()).flatMap(sound -> Minecraft.getInstance().getResourceManager().getResource(sound.getPath())).flatMap(resource -> {
                    try {
                        return resource.metadata().getSection(MusicTransitionMetadataSection.TYPE);
                    } catch (IOException e) {
                        return Optional.empty();
                    }
                });
                if(optionalMeta.isPresent()){
                    var meta = optionalMeta.get();
                    this.crossFadeDuration = meta.crossFadeDuration;
                    this.crossFadeStartTime = meta.crossFadeStartTime;
                    this.crossFadeTargetTime = meta.crossFadeTargetTime;
                }else{
                    this.crossFadeDuration = predefinedCrossFadeDuration;
                    this.crossFadeStartTime = predefinedCrossFadeStartTime;
                    this.crossFadeTargetTime = 0;
                }
                initialized = true;
            }
        }
        markedDeactivate = false;
    }

    public void deactivate(){
        if(!active) return;
        markedDeactivate = true;
    }

    public void clientTick(){
        if(!active) return;
        if(markedDeactivate){
            var noTrackActive = true;
            for (int i = 0; i < tracks.length; i++) {
                if(tracks[i] != null){
                    if(Minecraft.getInstance().getSoundManager().isActive(tracks[i])){
                        tracks[i].fadeToVolume(0);
                        tracks[i].fadeVolume();
                        if(tracks[i].getBaseVolume() <= 1e-2){
                            Minecraft.getInstance().getSoundManager().stop(tracks[i]);
                            tracks[i] = null;
                        }
                        noTrackActive = false;
                    }else{
                        tracks[i] = null;
                    }
                }
            }
            if(noTrackActive){
                active = false;
                markedDeactivate = false;
            }
        }else{
            if(tracks[activeTrack] != null) {
                if(!Minecraft.getInstance().getSoundManager().isActive(tracks[activeTrack])){
                    tracks[activeTrack] = BossMusicSoundInstance.fromMusic(soundEvent.value());
                    Minecraft.getInstance().getSoundManager().play(tracks[activeTrack]);
                    return;
                }
                tracks[activeTrack].fadeVolume();
                SoundEngineAccessor soundEngineAccessor = (SoundEngineAccessor) ((SoundManagerAccessor) Minecraft.getInstance().getSoundManager()).getSoundEngine();
                ChannelAccess.ChannelHandle handle = soundEngineAccessor.getInstanceToChannel().get(tracks[activeTrack]);
                if(handle != null && !handle.isStopped()){
                    Channel channel1 = ((ChannelHandleAccessor) handle).getChannel();
                    if(channel1.stopped()){
                        tracks[activeTrack] = null;
                        int nextTrack = (activeTrack + 1) % tracks.length;
                        tracks[nextTrack] = BossMusicSoundInstance.fromMusic(soundEvent.value());
                        Minecraft.getInstance().getSoundManager().play(tracks[nextTrack]);
                        activeTrack = nextTrack;
                    }else{
                        float sec = ((ISoundPositionVisitor) channel1).getSoundPositionSec();
                        if(sec >= crossFadeStartTime && sec <= crossFadeStartTime + crossFadeDuration){
                            int nextTrack = (activeTrack + 1) % tracks.length;
                            if(tracks[nextTrack] == null){
                                tracks[nextTrack] = BossMusicSoundInstance.fromMusic(soundEvent.value());
                                SoundEngine.PlayResult playResult = Minecraft.getInstance().getSoundManager().play(tracks[nextTrack]);
                                if(playResult == SoundEngine.PlayResult.STARTED || playResult == SoundEngine.PlayResult.STARTED_SILENTLY){
                                    ChannelAccess.ChannelHandle handle1 = soundEngineAccessor.getInstanceToChannel().get(tracks[nextTrack]);
                                    if(handle1 != null){
                                        handle1.execute(channel -> ((ISoundPositionVisitor) channel).setSoundPositionSec(sec - crossFadeStartTime + crossFadeTargetTime));
                                    }
                                }
                            }
                            var progress = Mth.clamp((float) (sec - crossFadeStartTime) / crossFadeDuration, 0, 1);
                            tracks[activeTrack].setVolume(1 - progress);
                            tracks[nextTrack].setVolume(progress);
                            if(sec >= crossFadeStartTime + crossFadeDuration){
                                tracks[activeTrack] = null;
                                activeTrack = nextTrack;
                            }
                        }else{
                            int oldActiveTrack = activeTrack;
                            int nextTrack = (activeTrack + 1) % tracks.length;
                            boolean shouldChange = false;
                            if(sec >= crossFadeStartTime + crossFadeDuration){
                                activeTrack = nextTrack;
                                shouldChange = true;
                            }
                            for (int i = 0; i < tracks.length; i++) {
                                if(tracks[i] != null && tracks[i].getDesiredVolume() != 1){
                                    tracks[i].setVolume(i == activeTrack ? 1 : 0);
                                }
                            }
                            if(shouldChange){
                                tracks[oldActiveTrack] = null;
                            }
                        }
                    }
                }else{
                    tracks[activeTrack] = null;
                    int nextTrack = (activeTrack + 1) % tracks.length;
                    tracks[nextTrack] = BossMusicSoundInstance.fromMusic(soundEvent.value());
                    Minecraft.getInstance().getSoundManager().play(tracks[nextTrack]);
                    activeTrack = nextTrack;
                }
            }
        }
        for (BossMusicSoundInstance track : tracks) {
            if(track != null){
                SoundEngineAccessor soundEngineAccessor = (SoundEngineAccessor) ((SoundManagerAccessor) Minecraft.getInstance().getSoundManager()).getSoundEngine();
                ChannelAccess.ChannelHandle handle = soundEngineAccessor.getInstanceToChannel().get(track);
                if(handle != null){
                    handle.execute(channel -> {
                        var volume = soundEngineAccessor.invokeCalculateVolume(track);
                        channel.setVolume(volume);
                    });
                }
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public boolean isMarkedDeactivate(){
        return markedDeactivate;
    }
}
