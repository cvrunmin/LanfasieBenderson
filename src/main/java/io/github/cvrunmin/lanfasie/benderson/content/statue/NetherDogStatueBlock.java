package io.github.cvrunmin.lanfasie.benderson.content.statue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class NetherDogStatueBlock extends FiveGuysStatueBlock{
    private static final VoxelShape LOWER_SHAPE = Shapes.or(
            box(0, 0, 0, 16, 2, 16),
            box(1, 2, 1, 15, 16, 15));

    private static final VoxelShape UPPER_SHAPE = Shapes.or(
            box(1, 0, 1, 15, 12, 15));

    private static final Map<Direction, VoxelShape> LOWER_SHAPES = Shapes.rotateHorizontal(LOWER_SHAPE);
    private static final Map<Direction, VoxelShape> UPPER_SHAPES = Shapes.rotateHorizontal(UPPER_SHAPE);

    public NetherDogStatueBlock(Properties properties) {
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
}
