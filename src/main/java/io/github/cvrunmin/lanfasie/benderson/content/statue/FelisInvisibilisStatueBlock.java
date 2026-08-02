package io.github.cvrunmin.lanfasie.benderson.content.statue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FelisInvisibilisStatueBlock extends FiveGuysStatueBlock{
    public FelisInvisibilisStatueBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getUpperShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, Direction facing) {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getLowerShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, Direction facing) {
        return Shapes.block();
    }
}
