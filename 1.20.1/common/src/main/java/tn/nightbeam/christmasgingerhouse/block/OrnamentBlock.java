package tn.nightbeam.christmasgingerhouse.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nullable;

/** A glass bauble. Hangs from the block above (leaves included) or sits on the block below. */
public class OrnamentBlock extends Block {

    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    private static final VoxelShape STANDING = Block.box(5, 0, 5, 11, 8, 11);
    private static final VoxelShape HUNG = Block.box(5, 6, 5, 11, 16, 11);

    public OrnamentBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(HANGING, true));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                BlockState state = defaultBlockState().setValue(HANGING, direction == Direction.UP);
                if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
                    return state;
                }
            }
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HANGING)) {
            BlockPos above = pos.above();
            return Block.canSupportCenter(level, above, Direction.DOWN) || level.getBlockState(above).is(BlockTags.LEAVES);
        }
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction attached = state.getValue(HANGING) ? Direction.UP : Direction.DOWN;
        return direction == attached && !canSurvive(state, level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HANGING) ? HUNG : STANDING;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HANGING);
    }
}
