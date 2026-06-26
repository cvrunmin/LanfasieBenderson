package io.github.cvrunmin.lanfasie.benderson.content.lanfasie;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.constant.DefaultAnimations;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

public class LanfasieEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> ANIMATE_STATE = SynchedEntityData.defineId(LanfasieEntity.class, EntityDataSerializers.STRING);

    record SongNotes(int[] notes, int[] noteDeltaTick, int duration){
        public int actualNoteLength(){
            return Math.min(notes.length, noteDeltaTick.length);
        }
    }

    private static final List<SongNotes> SONG_TRACKS = List.of(
            new SongNotes(new int[]{7, 10, 15, 17, 14, 10, 7, 10, 15, 14, 10, 7},
                          new int[]{0, 5, 10, 30, 35, 40, 60, 65, 70, 90, 95, 100},
                          120),
            new SongNotes(new int[]{13, 13, 16, 15, 13, 13, 16, 15, 13, 13, 16, 15, 13, 13, 16, 15},
                          new int[]{0, 5, 10, 13, 20, 25, 30, 33, 40, 45, 50, 53, 60, 65, 70, 73},
                          80),
            new SongNotes(new int[]{13, 16, 15, 13, 11,  3,  9, 16, 15, 13, 11,  3,  6, 16, 15, 13, 11,  3,  4,  16,  15,  13,  11,  13},
                          new int[]{ 0,  6, 12, 16, 22, 28, 32, 38, 44, 48, 54, 60, 64, 70, 76, 80, 86, 92, 96, 102, 108, 112, 118, 124},
                          128)
            );

    private int playingSongIndex = -1;
    private int songProgressTick = 0;
    private int songProgressIndex = -1;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public LanfasieEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(ANIMATE_STATE, "idle");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, (double)200f)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.FLYING_SPEED, 0.6)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9f)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 1.0f)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController());
        controllers.add(new AnimationController<LanfasieEntity>("Special Perform", test -> {
            var animateState = test.getDataOrDefault(LanfasieDataTickets.ANIMATE_STATE, "idle");
            if(animateState.equals("play_lute")){
                return test.setAndContinue(RawAnimation.begin().thenPlay("lanfasie.play_lute"));
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public String getAnimateState() {
        return entityData.get(ANIMATE_STATE);
    }

    public void setAnimateState(String state){
        entityData.set(ANIMATE_STATE, state);
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide()){
            if(getAnimateState().equals("play_lute")){
                if(playingSongIndex == -1){
                    if (tickCount % 20 == 0 && level().getRandom().nextFloat() < 0.25) {
                        playingSongIndex = level().getRandom().nextInt(SONG_TRACKS.size());
                        songProgressTick = 0;
                        songProgressIndex = 0;
                        var songTrack = SONG_TRACKS.get(playingSongIndex);
                        int note = songTrack.notes()[songProgressIndex];
                        playNote(note);
                    }
                }else{
                    songProgressTick++;
                    var songTrack = SONG_TRACKS.get(playingSongIndex);
                    if(songProgressTick > songTrack.duration){
                        playingSongIndex = -1;
                        songProgressTick = 0;
                        songProgressIndex = -1;
                    }else{
                        for (int i = songProgressIndex + 1; i < songTrack.actualNoteLength(); i++) {
                            if(songTrack.noteDeltaTick[i] == songProgressTick){
                                songProgressIndex = i;
                                playNote(songTrack.notes[i]);
                            }else if(songTrack.noteDeltaTick[i] > songProgressTick){
                                break;
                            }
                        }
                    }
                }
            }else{
                playingSongIndex = -1;
                songProgressTick = 0;
                songProgressIndex = -1;
            }
        }
    }

    private void playNote(int note) {
        if(level().isClientSide()){
            level().playLocalSound(this, SoundEvents.NOTE_BLOCK_GUITAR.value(), SoundSource.NEUTRAL, 3.0f, NoteBlock.getPitchFromNote(note));
            var particlePos = position().add(getLookAngle().horizontal().scale(0.5)).add(0, getY(0.5) - getY(), 0);
            level().addParticle(ParticleTypes.NOTE, particlePos.x, particlePos.y, particlePos.z, note / 24.0, 0.0, 0.0);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        input.getString("AnimateState").ifPresent(this::setAnimateState);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("AnimateState", getAnimateState());
    }
}
