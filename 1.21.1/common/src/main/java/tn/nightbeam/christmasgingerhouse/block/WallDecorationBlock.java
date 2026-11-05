package tn.nightbeam.christmasgingerhouse.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/** A flat decoration hung on the side of a block (wreaths, stockings, lights). */
public class WallDecorationBlock extends HorizontalDirectionalBlock {

    private final Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
    private final MapCodec<WallDecorationBlock> codec;

    /** @param depth thickness in pixels, measured from the wall */
    public WallDecorationBlock(Properties properties, double depth) {
        super(properties);
        codec = simpleCodec(p -> new WallDecorationBlock(p, depth));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
        shapes.put(Direction.NORTH, Block.box(1, 1, 16 - depth, 15, 15, 16));
        shapes.put(Direction.SOUTH, Block.box(1, 1, 0, 15, 15, depth));
        shapes.put(Direction.WEST, Block.box(16 - depth, 1, 1, 16, 15, 15));
        shapes.put(Direction.EAST, Block.box(0, 1, 1, depth, 15, 15));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return codec;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.get(state.getValue(FACING));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos behind = pos.relative(facing.getOpposite());
        BlockState support = level.getBlockState(behind);
        // Leaves have an empty support shape, but decorating trees is the point.
        return support.isFaceSturdy(level, behind, facing) || support.is(BlockTags.LEAVES);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
                BlockState state = defaultBlockState().setValue(FACING, direction.getOpposite());
                if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
                    return state;
                }
            }
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return direction.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
