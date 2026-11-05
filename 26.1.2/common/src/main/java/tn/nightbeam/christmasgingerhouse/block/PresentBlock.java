package tn.nightbeam.christmasgingerhouse.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
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

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 12.0, 15.0);
    private static final int[] CONFETTI = {0xEB2938, 0x299E4A, 0xFFD94A, 0xF2F2FF, 0x4D8AF0};

    public PresentBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public static final MapCodec<PresentBlock> CODEC = simpleCodec(PresentBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
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
                0.95F + level.getRandom().nextFloat() * 0.1F);
        double x = pos.getX() + 0.5, y = pos.getY() + 0.6, z = pos.getZ() + 0.5;
        for (int colour : CONFETTI) {
            level.sendParticles(new DustParticleOptions(colour, 1.1F), x, y, z, 9, 0.35, 0.3, 0.35, 0.15);
        }
        level.sendParticles(ParticleTypes.FIREWORK, x, y + 0.2, z, 6, 0.2, 0.2, 0.2, 0.08);
        if (player instanceof ServerPlayer serverPlayer) {
            if (sender != null && !sender.equals(player.getGameProfile().name())) {
                serverPlayer.sendOverlayMessage(Component.translatable("block.christmas_ginger_house.present.from", sender));
            }
            Advancements.award(serverPlayer, "unwrap_present");
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        // Creative players get the wrapped present back too, like shulker boxes.
        if (!level.isClientSide() && player.isCreative() && level.getBlockEntity(pos) instanceof PresentBlockEntity present
                && present.hasGift()) {
            ItemStack stack = new ItemStack(this);
            stack.applyComponents(present.collectComponents());
            net.minecraft.world.entity.item.ItemEntity drop = new net.minecraft.world.entity.item.ItemEntity(level,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            drop.setDefaultPickUpDelay();
            level.addFreshEntity(drop);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return net.minecraft.world.inventory.AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction,
                                     BlockPos neighborPos, BlockState neighbor, RandomSource random) {
        return direction == Direction.DOWN && !canSurvive(state, level, pos)
                ? net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()
                : super.updateShape(state, level, ticks, pos, direction, neighborPos, neighbor, random);
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
