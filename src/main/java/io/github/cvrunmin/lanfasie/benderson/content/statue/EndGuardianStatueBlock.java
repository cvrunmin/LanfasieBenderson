package io.github.cvrunmin.lanfasie.benderson.content.statue;

import io.github.cvrunmin.lanfasie.benderson.content.benderson.Benderson;
import io.github.cvrunmin.lanfasie.benderson.content.unforgiven.*;
import io.github.cvrunmin.lanfasie.benderson.index.AllBlocks;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EndGuardianStatueBlock extends FiveGuysStatueBlock {
    private static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(0, 0, 0, 16, 2, 16),
            box(4.5, 2, 8.25, 11.5, 16, 15.25));

    private static final VoxelShape UPPER_SHAPE = Shapes.or(
            box(4.5, 0, 8.25, 11.5, 15, 15.25),
            box(1, 5, 9.5, 15, 9, 13.5),
            box(2, 4, 7, 14, 8, 11),
            box(3.5, 3, 4.5, 12.5, 7, 8.5),
            box(5, 2.5, 2, 11, 6.5, 6));


    private static final Map<Direction, VoxelShape> LOWER_SHAPES = Shapes.rotateHorizontal(LOWER_SHAPE);
    private static final Map<Direction, VoxelShape> UPPER_SHAPES = Shapes.rotateHorizontal(UPPER_SHAPE);

    public EndGuardianStatueBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getUpperShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, Direction facing) {
        return UPPER_SHAPES.get(facing);
    }

    @Override
    protected VoxelShape getLowerShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, Direction facing) {
        return LOWER_SHAPES.get(facing);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if(!movedByPiston && !level.isClientSide() && state.getValue(FiveGuysStatueBlock.HALF) == DoubleBlockHalf.LOWER){
            List<Benderson> entities = level.getEntitiesOfClass(Benderson.class, AABB.ofSize(Vec3.atLowerCornerOf(pos), 48, 18, 48), candidate -> !candidate.isNoAi());
            if(!entities.isEmpty()) return;
            boolean[] hasOtherStatue = new boolean[4];
            BlockPos[] otherStatuePos = new BlockPos[4];
            Arrays.fill(otherStatuePos, new BlockPos(pos));
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                for (int i = 1; i <= 2; i++) {
                    var offsetPos = pos.relative(direction, i);
                    var offsetState = level.getBlockState(offsetPos);
                    if (offsetState.getOptionalValue(FiveGuysStatueBlock.HALF).map(v -> v != DoubleBlockHalf.LOWER).orElse(false)) {
                        continue;
                    }
                    if(offsetState.is(AllBlocks.FELIS_INVISIBILIS_STATUE)) {
                        hasOtherStatue[0] = true;
                        otherStatuePos[0] = offsetPos;
                        break;
                    }
                    if(offsetState.is(AllBlocks.NETHER_DOG_STATUE)) {
                        hasOtherStatue[1] = true;
                        otherStatuePos[1] = offsetPos;
                        break;
                    }
                    if(offsetState.is(AllBlocks.HYDRO_DREAMER_STATUE)) {
                        hasOtherStatue[2] = true;
                        otherStatuePos[2] = offsetPos;
                        break;
                    }
                    if(offsetState.is(AllBlocks.VOID_HARE_STATUE)) {
                        hasOtherStatue[3] = true;
                        otherStatuePos[3] = offsetPos;
                        break;
                    }
                }
            }
            if (!IntStream.range(0, 4).allMatch(i -> hasOtherStatue[i])) {
                Arrays.fill(hasOtherStatue, false);
                Arrays.fill(otherStatuePos, new BlockPos(pos));
                boolean[] quadrantHasStatue = new boolean[4];
                for (int quadrant = 0; quadrant < 4; quadrant++) {
                    statueSearch:
                    for (int i1 = 1; i1 <= 2; i1++) {
                        for (int j1 = 1; j1 <= 2; j1++) {
                            if(quadrantHasStatue[quadrant]) break statueSearch;
                            int i = quadrant == 1 || quadrant == 2 ? -i1 : i1;
                            int j = quadrant < 2 ? -j1 : j1;
                            var offsetPos = pos.offset(i, 0, j);
                            var offsetState = level.getBlockState(offsetPos);
                            if (offsetState.getOptionalValue(FiveGuysStatueBlock.HALF).map(v -> v != DoubleBlockHalf.LOWER).orElse(false)) {
                                continue;
                            }
                            if(offsetState.is(AllBlocks.FELIS_INVISIBILIS_STATUE)) {
                                hasOtherStatue[0] = true;
                                otherStatuePos[0] = offsetPos;
                                quadrantHasStatue[quadrant] = true;
                            }
                            if(offsetState.is(AllBlocks.NETHER_DOG_STATUE)) {
                                hasOtherStatue[1] = true;
                                otherStatuePos[1] = offsetPos;
                                quadrantHasStatue[quadrant] = true;
                            }
                            if(offsetState.is(AllBlocks.HYDRO_DREAMER_STATUE)) {
                                hasOtherStatue[2] = true;
                                otherStatuePos[2] = offsetPos;
                                quadrantHasStatue[quadrant] = true;
                            }
                            if(offsetState.is(AllBlocks.VOID_HARE_STATUE)) {
                                hasOtherStatue[3] = true;
                                otherStatuePos[3] = offsetPos;
                                quadrantHasStatue[quadrant] = true;
                            }
                        }
                    }
                }
            }
            if (IntStream.range(0, 4).allMatch(i -> hasOtherStatue[i])) {
                for (BlockPos pos1 : otherStatuePos) {
                    level.destroyBlock(pos1, false);
                }
                level.destroyBlock(pos, false);
                var un1 = new UnforgivenIndiscretion(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                var un2 = new UnforgivenSpoiling(level, otherStatuePos[3].getX() + 0.5, otherStatuePos[3].getY(), otherStatuePos[3].getZ() + 0.5);
                var un3 = new UnforgivenCowardice(level, otherStatuePos[2].getX() + 0.5, otherStatuePos[2].getY(), otherStatuePos[2].getZ() + 0.5);
                var un4 = new UnforgivenRidicule(level, otherStatuePos[1].getX() + 0.5, otherStatuePos[1].getY(), otherStatuePos[1].getZ() + 0.5);
                var un5 = new UnforgivenPerfidy(level, otherStatuePos[0].getX() + 0.5, otherStatuePos[0].getY(), otherStatuePos[0].getZ() + 0.5);
                un1.lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(0, 0, -1).add(Vec3.atBottomCenterOf(pos)));
                un2.lookAt(EntityAnchorArgument.Anchor.FEET, Vec3.atBottomCenterOf(pos));
                un3.lookAt(EntityAnchorArgument.Anchor.FEET, Vec3.atBottomCenterOf(pos));
                un4.lookAt(EntityAnchorArgument.Anchor.FEET, Vec3.atBottomCenterOf(pos));
                un5.lookAt(EntityAnchorArgument.Anchor.FEET, Vec3.atBottomCenterOf(pos));
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
                Benderson benderson = new Benderson(level, pos.getX(), pos.getY(), pos.getZ());
                benderson.setBodyState(Benderson.BodyState.ENTRANCE);
                benderson.setPhaseState("arena_entering");
                level.addFreshEntity(benderson);
                level.gameEvent(GameEvent.ENTITY_PLACE, pos, GameEvent.Context.of(state));
            } else {
                return;
            }
        }
    }
}
