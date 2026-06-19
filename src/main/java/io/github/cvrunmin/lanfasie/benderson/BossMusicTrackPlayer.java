package io.github.cvrunmin.lanfasie.benderson;

import io.github.cvrunmin.lanfasie.benderson.mixin.SoundEngineAccessor;
import io.github.cvrunmin.lanfasie.benderson.mixin.SoundManagerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;

public class BossMusicTrackPlayer {
    private final Holder<SoundEvent> soundEvent;
    private final int musicDuration;
    private final int crossFadeDuration;
    private final int crossFadeStartTime;

    private boolean active = false;
    private boolean markedDeactivate = false;
    private BossMusicSoundInstance[] tracks = new BossMusicSoundInstance[2];
    private int activeTrack = 0;
    private int tick = 0;

    public BossMusicTrackPlayer(Holder<SoundEvent> soundEvent, float musicDuration, float crossFadeDuration, float crossFadeStartTime){
        if(crossFadeStartTime > musicDuration){
            throw new IllegalArgumentException("cross-fade start %f > music duration %f".formatted(crossFadeStartTime, musicDuration));
        }
        this.soundEvent = soundEvent;
        this.musicDuration = (int) (musicDuration * 20);
        this.crossFadeDuration = (int) (crossFadeDuration * 20);
        this.crossFadeStartTime = (int) (crossFadeStartTime * 20);
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
            tick = 0;
            Minecraft.getInstance().getSoundManager().play(tracks[0]);
        }
        markedDeactivate = false;
    }

    public void deactivate(){
        if(!active) return;
        markedDeactivate = true;
    }

    public void clientTick(){
        if(!active) return;
        tick++;
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
                    tick = 0;
                    return;
                }
                tracks[activeTrack].fadeVolume();
            }
            if(tick >= crossFadeStartTime && tick <= crossFadeStartTime + crossFadeDuration){
                int nextTrack = (activeTrack + 1) % tracks.length;
                if(tracks[nextTrack] == null){
                    tracks[nextTrack] = BossMusicSoundInstance.fromMusic(soundEvent.value());
                    Minecraft.getInstance().getSoundManager().play(tracks[nextTrack]);
                }
                var progress = Mth.clamp((float) (tick - crossFadeStartTime) / crossFadeDuration, 0, 1);
                tracks[activeTrack].setVolume(1 - progress);
                tracks[nextTrack].setVolume(progress);

                if(tick == crossFadeStartTime + crossFadeDuration){
                    tick = crossFadeDuration;
                    activeTrack = nextTrack;
                }
            } else {
                for (int i = 0; i < tracks.length; i++) {
                    if(tracks[i] != null && tracks[i].getDesiredVolume() != 1){
                        tracks[i].setVolume(i == activeTrack ? 1 : 0);
                    }
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
