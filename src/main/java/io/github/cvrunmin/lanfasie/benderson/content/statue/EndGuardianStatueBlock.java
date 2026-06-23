package io.github.cvrunmin.lanfasie.benderson.content.statue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class EndGuardianStatueBlock extends Block {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

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
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        DoubleBlockHalf half = state.getValue(HALF);
        if(half == DoubleBlockHalf.UPPER) return UPPER_SHAPES.get(direction);
        return LOWER_SHAPES.get(direction);
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return super.getInteractionShape(state, level, pos);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess ticks,
            BlockPos pos,
            Direction directionToNeighbour,
            BlockPos neighbourPos,
            BlockState neighbourState,
            RandomSource random
    ) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (directionToNeighbour.getAxis() != Direction.Axis.Y || half == DoubleBlockHalf.LOWER != (directionToNeighbour == Direction.UP)) {
            return half == DoubleBlockHalf.LOWER && directionToNeighbour == Direction.DOWN && !state.canSurvive(level, pos)
                    ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
        } else {
            return neighbourState.getBlock() instanceof EndGuardianStatueBlock && neighbourState.getValue(HALF) != half
                    ? neighbourState.setValue(HALF, half)
                    : Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && (player.isCreative() || !player.hasCorrectToolForDrops(state, level, pos))) {
            DoubleBlockHalf part = state.getValue(HALF);
            if (part == DoubleBlockHalf.UPPER) {
                BlockPos bottomPos = pos.below();
                BlockState bottomState = level.getBlockState(bottomPos);
                if (bottomState.is(state.getBlock()) && bottomState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    BlockState blockState = bottomState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                    level.setBlock(bottomPos, blockState, 35);
                    level.levelEvent(player, 2001, bottomPos, Block.getId(bottomState));
                }
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() < level.getMaxY() && level.getBlockState(pos.above()).canBeReplaced(context)) {
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection().getOpposite())
                    .setValue(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? belowState.isFaceSturdy(level, below, Direction.UP) : belowState.is(this);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected long getSeed(BlockState state, BlockPos pos) {
        return Mth.getSeed(pos.getX(), pos.below(state.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING);
    }
}
