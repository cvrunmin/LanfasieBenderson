package io.github.cvrunmin.lanfasie.benderson.content;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.unforgiven.*;
import io.github.cvrunmin.lanfasie.benderson.foundation.BossSummonInfo;
import io.github.cvrunmin.lanfasie.benderson.index.AllAttributes;
import io.github.cvrunmin.lanfasie.benderson.index.AllBlocks;
import io.github.cvrunmin.lanfasie.benderson.index.AllDataComponents;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class OminousOrbItem extends Item {
    public OminousOrbItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState blockState = level.getBlockState(pos);
        if(!blockState.is(AllBlocks.DEEP_LATENT_CALLER)) return InteractionResult.FAIL;
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);
        if (!aboveState.getCollisionShape(level, above).isEmpty()) {
            return InteractionResult.FAIL;
        }
        List<Benderson> entities = level.getEntitiesOfClass(Benderson.class, AABB.ofSize(Vec3.atLowerCornerOf(above), 48, 18, 48), candidate -> !candidate.isNoAi());
        if(!entities.isEmpty()) return InteractionResult.FAIL;
        if (level instanceof ServerLevel) {
            var un1 = new UnforgivenIndiscretion(level, above.getX(), above.getY(), above.getZ());
            var un2 = new UnforgivenSpoiling(level, above.getX() + 2, above.getY(), above.getZ() + 2);
            var un3 = new UnforgivenCowardice(level, above.getX() - 2, above.getY(), above.getZ() + 2);
            var un4 = new UnforgivenRidicule(level, above.getX() - 2, above.getY(), above.getZ() - 2);
            var un5 = new UnforgivenPerfidy(level, above.getX() + 2, above.getY(), above.getZ() - 2);
            un1.lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(0, 0, -1).add(Vec3.atLowerCornerOf(above)));
            un2.lookAt(EntityAnchorArgument.Anchor.FEET, Vec3.atLowerCornerOf(above));
            un3.lookAt(EntityAnchorArgument.Anchor.FEET, Vec3.atLowerCornerOf(above));
            un4.lookAt(EntityAnchorArgument.Anchor.FEET, Vec3.atLowerCornerOf(above));
            un5.lookAt(EntityAnchorArgument.Anchor.FEET, Vec3.atLowerCornerOf(above));
            un1.setNoAi(true);
            un2.setNoAi(true);
            un3.setNoAi(true);
            un4.setNoAi(true);
            un5.setNoAi(true);
            level.addFreshEntity(un1);
            level.addFreshEntity(un2);
            level.addFreshEntity(un3);
            level.addFreshEntity(un4);
            level.addFreshEntity(un5);
            var bossSummonInfo = Optional.ofNullable(context.getItemInHand().getComponents().get(AllDataComponents.BOSS_SUMMON_INFO.get()))
                    .orElseGet(() -> BossSummonInfo.builder().setArenaRadius(context.getItemInHand().getComponents().getOrDefault(AllDataComponents.ARENA_RADIUS.get(), 24)).build());
            Benderson benderson = new Benderson(level, above.getX(), above.getY(), above.getZ(), bossSummonInfo.arenaRadius());
            AttributeInstance damageGateAttr = benderson.getAttribute(AllAttributes.DAMAGE_GATE_PERCENTAGE);
            if(damageGateAttr != null) {
                damageGateAttr.setBaseValue(bossSummonInfo.damageGate());
            }
            benderson.setBodyState(Benderson.BodyState.ENTRANCE);
            benderson.setPhaseState("arena_entering");
            level.addFreshEntity(benderson);
            level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, above);
        }
        context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }
}
