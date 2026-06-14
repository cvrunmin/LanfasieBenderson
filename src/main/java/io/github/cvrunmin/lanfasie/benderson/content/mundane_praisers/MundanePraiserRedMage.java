package io.github.cvrunmin.lanfasie.benderson.content.mundane_praisers;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.constant.DefaultAnimations;
import com.geckolib.util.GeckoLibUtil;
import io.github.cvrunmin.lanfasie.benderson.index.AllItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jspecify.annotations.Nullable;

public class MundanePraiserRedMage extends Mob implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public MundanePraiserRedMage(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericIdleController());
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.FLYING_SPEED, 1)
                .add(Attributes.CAMERA_DISTANCE, (double)16.0F)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0f)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 1.0f)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0f)
                ;

    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(level, difficulty, spawnReason, groupData);
        setLeftHanded(false);
        populateDefaultEquipmentSlots(random, difficulty);
        return spawnGroupData;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        setItemSlot(EquipmentSlot.MAINHAND, AllItems.MUNDANE_PRAISER_RAPIER.toStack());
        setItemSlot(EquipmentSlot.OFFHAND, AllItems.MUNDANE_PRAISER_MANA_FOCI.toStack());
    }


    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean killedByPlayer) {

    }
}
