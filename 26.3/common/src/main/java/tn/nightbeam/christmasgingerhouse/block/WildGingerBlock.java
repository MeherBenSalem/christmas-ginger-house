package tn.nightbeam.christmasgingerhouse.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Wild ginger with its red flower spike. Grows in snowy forests and plains; drops ginger root. */
public class WildGingerBlock extends VegetationBlock {

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 13, 14);

    public WildGingerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        // On 26.x the #minecraft:dirt check alone refused grass, so wild ginger never generated; the vanilla check
        // (super) knows the soils plants grow on, so keep it and still allow dirt and farmland.
        return super.mayPlaceOn(state, level, pos)
                || state.is(BlockTags.DIRT) || state.is(net.minecraft.world.level.block.Blocks.FARMLAND);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(pos);
        return SHAPE.move(offset.x, offset.y, offset.z);
    }
}
