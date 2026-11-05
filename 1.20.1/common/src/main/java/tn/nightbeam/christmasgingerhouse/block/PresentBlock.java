package tn.nightbeam.christmasgingerhouse.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nullable;
import org.joml.Vector3f;
import tn.nightbeam.christmasgingerhouse.registry.ModSounds;
import tn.nightbeam.christmasgingerhouse.util.Advancements;

/**
 * A wrapped present.
 * <ul>
 *     <li>Use an empty present to open its 3x3 wrapping grid and fill it.</li>
 *     <li>Use a wrapped present to unwrap it: the gift pops out with confetti.</li>
 *     <li>Sneak-use a wrapped present to peek inside and change the gift.</li>
 *     <li>Break it to carry it, gift and all, to a friend.</li>
 * </ul>
 */
public class PresentBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 12.0, 15.0);
    private static final Vector3f[] CONFETTI = {
            new Vector3f(0.92F, 0.16F, 0.22F), new Vector3f(0.16F, 0.62F, 0.29F),
            new Vector3f(1.0F, 0.85F, 0.29F), new Vector3f(0.95F, 0.95F, 1.0F), new Vector3f(0.3F, 0.54F, 0.94F)};

    public PresentBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof PresentBlockEntity present)) {
            return InteractionResult.PASS;
        }
        if (present.hasGift() && !player.isShiftKeyDown()) {
            unwrap((ServerLevel) level, pos, present, player);
            return InteractionResult.CONSUME;
        }
        if (player.isSpectator()) {
            return InteractionResult.PASS;
        }
        player.openMenu(present);
        level.playSound(null, pos, ModSounds.PRESENT_WRAP.get(), SoundSource.BLOCKS, 0.6F, 1.0F);
        return InteractionResult.CONSUME;
    }

    private static void unwrap(ServerLevel level, BlockPos pos, PresentBlockEntity present, Player player) {
        String sender = present.getSender();
        present.unpackLootTable(player);
        Containers.dropContents(level, pos, present);
        present.clearContent();
        level.removeBlock(pos, false);
        level.playSound(null, pos, ModSounds.PRESENT_UNWRAP.get(), SoundSource.BLOCKS, 1.0F,
                0.95F + level.random.nextFloat() * 0.1F);
        double x = pos.getX() + 0.5, y = pos.getY() + 0.6, z = pos.getZ() + 0.5;
        for (Vector3f colour : CONFETTI) {
            level.sendParticles(new DustParticleOptions(colour, 1.1F), x, y, z, 9, 0.35, 0.3, 0.35, 0.15);
        }
        level.sendParticles(ParticleTypes.FIREWORK, x, y + 0.2, z, 6, 0.2, 0.2, 0.2, 0.08);
        if (player instanceof ServerPlayer serverPlayer) {
            if (sender != null && !sender.equals(player.getGameProfile().getName())) {
                serverPlayer.displayClientMessage(Component.translatable("block.christmas_ginger_house.present.from", sender), true);
            }
            Advancements.award(serverPlayer, "unwrap_present");
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        // Contents travel inside the dropped item (see the block loot table), so nothing spills here.
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        // Creative players get the wrapped present back too, like shulker boxes.
        if (!level.isClientSide && player.isCreative() && level.getBlockEntity(pos) instanceof PresentBlockEntity present
                && present.hasGift()) {
            ItemStack stack = new ItemStack(this);
            present.saveToItem(stack);
            net.minecraft.world.entity.item.ItemEntity drop = new net.minecraft.world.entity.item.ItemEntity(level,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            drop.setDefaultPickUpDelay();
            level.addFreshEntity(drop);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return net.minecraft.world.inventory.AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN && !canSurvive(state, level, pos)
                ? net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PresentBlockEntity(pos, state);
    }
}
