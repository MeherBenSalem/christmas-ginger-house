package tn.nightbeam.christmasgingerhouse.block;

import net.minecraft.ChatFormatting;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import tn.nightbeam.christmasgingerhouse.Constants;
import tn.nightbeam.christmasgingerhouse.registry.ModSounds;
import tn.nightbeam.christmasgingerhouse.util.Advancements;
import tn.nightbeam.christmasgingerhouse.world.AdventCalendarData;

/**
 * Twenty-four little doors. Each player may open one per Minecraft day, from any calendar,
 * and gets a treat from {@code gameplay/advent_calendar/door}. Door 24 opens the
 * {@code gameplay/advent_calendar/christmas_eve} table instead.
 */
public class AdventCalendarBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<AdventCalendarBlock> CODEC = simpleCodec(AdventCalendarBlock::new);

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public static final ResourceKey<LootTable> DOOR_LOOT = ResourceKey.create(Registries.LOOT_TABLE, Constants.id("gameplay/advent_calendar/door"));
    public static final ResourceKey<LootTable> FINALE_LOOT = ResourceKey.create(Registries.LOOT_TABLE, Constants.id("gameplay/advent_calendar/christmas_eve"));

    public AdventCalendarBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayer serverPlayer) || player.isSpectator()) {
            return InteractionResult.PASS;
        }
        ServerLevel serverLevel = (ServerLevel) level;
        long day = serverLevel.getOverworldClockTime() / 24000L;
        AdventCalendarData.Attempt attempt = AdventCalendarData.get(serverLevel.getServer()).tryOpen(player.getUUID(), day);
        if (!attempt.opened()) {
            serverPlayer.sendOverlayMessage(Component.translatable("block.christmas_ginger_house.advent_calendar.wait",
                    attempt.door()).withStyle(ChatFormatting.GRAY));
            return InteractionResult.CONSUME;
        }

        boolean finale = attempt.door() == AdventCalendarData.DOORS;
        LootTable table = serverLevel.getServer().reloadableRegistries().getLootTable(finale ? FINALE_LOOT : DOOR_LOOT);
        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withLuck(player.getLuck())
                .create(LootContextParamSets.GIFT);
        for (ItemStack stack : table.getRandomItems(params)) {
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }

        Direction facing = state.getValue(FACING);
        double x = pos.getX() + 0.5 + facing.getStepX() * 0.55;
        double z = pos.getZ() + 0.5 + facing.getStepZ() * 0.55;
        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, pos.getY() + 0.6, z, 8, 0.25, 0.25, 0.25, 0.02);
        level.playSound(null, pos, ModSounds.ADVENT_CALENDAR_OPEN.get(), SoundSource.BLOCKS, 0.9F, 1.0F);
        serverPlayer.sendOverlayMessage(Component.translatable(finale
                ? "block.christmas_ginger_house.advent_calendar.finale"
                : "block.christmas_ginger_house.advent_calendar.opened", attempt.door()).withStyle(ChatFormatting.GOLD));
        Advancements.award(serverPlayer, "open_advent_door");
        if (finale) {
            Advancements.award(serverPlayer, "christmas_eve");
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
