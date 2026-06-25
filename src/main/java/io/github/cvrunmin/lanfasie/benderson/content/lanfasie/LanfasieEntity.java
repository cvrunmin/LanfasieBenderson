package io.github.cvrunmin.lanfasie.benderson.content.lanfasie;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.constant.DefaultAnimations;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class LanfasieEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<String> ANIMATE_STATE = SynchedEntityData.defineId(LanfasieEntity.class, EntityDataSerializers.STRING);
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
