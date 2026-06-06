package io.github.cvrunmin.lanfasie.benderson.content;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.index.AllBlocks;
import io.github.cvrunmin.lanfasie.benderson.index.AllDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

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
        if (!level.isEmptyBlock(above)) {
            return InteractionResult.FAIL;
        }
        List<Benderson> entities = level.getEntitiesOfClass(Benderson.class, AABB.ofSize(Vec3.atLowerCornerOf(above), 48, 10, 48));
        if(!entities.isEmpty()) return InteractionResult.FAIL;
        if (level instanceof ServerLevel) {
            var radius = context.getItemInHand().getComponents().getOrDefault(AllDataComponents.ARENA_RADIUS.get(), 24);
            Benderson benderson = new Benderson(level, above.getX(), above.getY(), above.getZ(), radius);
            benderson.setBodyState(Benderson.BodyState.ENTRANCE);
            benderson.setPhaseState("arena_entering");
            level.addFreshEntity(benderson);
            level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, above);
        }
        context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }
}
