package tn.nightbeam.christmasgingerhouse.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import tn.nightbeam.christmasgingerhouse.block.AdventCalendarBlock;
import tn.nightbeam.christmasgingerhouse.block.ChristmasLightsBlock;
import tn.nightbeam.christmasgingerhouse.block.GingerCropBlock;
import tn.nightbeam.christmasgingerhouse.block.GingerbreadDoorBlock;
import tn.nightbeam.christmasgingerhouse.block.GingerbreadTrapDoorBlock;
import tn.nightbeam.christmasgingerhouse.block.GingerbreadStairBlock;
import tn.nightbeam.christmasgingerhouse.block.OrnamentBlock;
import tn.nightbeam.christmasgingerhouse.block.PresentBlock;
import tn.nightbeam.christmasgingerhouse.block.StarTopperBlock;
import tn.nightbeam.christmasgingerhouse.block.WallDecorationBlock;
import tn.nightbeam.christmasgingerhouse.block.WildGingerBlock;
import tn.nightbeam.christmasgingerhouse.platform.Services;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.List;
import java.util.function.Supplier;

public final class ModBlocks {

    /** Every present colour, in creative-tab order. */
    public static final List<Supplier<PresentBlock>> PRESENTS = new ArrayList<>();
    private static final Map<Supplier<? extends Block>, String> NAMES = new IdentityHashMap<>();

    // --- Gingerbread building set
    public static final Supplier<Block> GINGERBREAD_BLOCK = register("gingerbread_block",
            () -> new Block(gingerbread()));
    public static final Supplier<Block> GINGERBREAD_BRICKS = register("gingerbread_bricks",
            () -> new Block(gingerbread()));
    public static final Supplier<StairBlock> GINGERBREAD_BRICK_STAIRS = register("gingerbread_brick_stairs",
            () -> new GingerbreadStairBlock(GINGERBREAD_BRICKS.get().defaultBlockState(), gingerbread()));
    public static final Supplier<SlabBlock> GINGERBREAD_BRICK_SLAB = register("gingerbread_brick_slab",
            () -> new SlabBlock(gingerbread()));
    public static final Supplier<WallBlock> GINGERBREAD_BRICK_WALL = register("gingerbread_brick_wall",
            () -> new WallBlock(gingerbread().forceSolidOn()));
    public static final Supplier<Block> FROSTED_GINGERBREAD_BRICKS = register("frosted_gingerbread_bricks",
            () -> new Block(gingerbread()));
    public static final Supplier<GingerbreadDoorBlock> GINGERBREAD_DOOR = register("gingerbread_door",
            () -> new GingerbreadDoorBlock(gingerbread().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<GingerbreadTrapDoorBlock> GINGERBREAD_TRAPDOOR = register("gingerbread_trapdoor",
            () -> new GingerbreadTrapDoorBlock(gingerbread().noOcclusion().isValidSpawn((s, l, p, e) -> false)));
    public static final Supplier<Block> ICING_BLOCK = register("icing_block",
            () -> new Block(Keys.blockProps().mapColor(MapColor.SNOW).strength(0.6F).sound(SoundType.WOOL)));
    public static final Supplier<RotatedPillarBlock> CANDY_CANE_BLOCK = register("candy_cane_block",
            () -> new RotatedPillarBlock(candy()));
    public static final Supplier<Block> PEPPERMINT_BLOCK = register("peppermint_block",
            () -> new Block(candy()));

    // --- Decorations
    public static final Supplier<ChristmasLightsBlock> CHRISTMAS_LIGHTS = register("christmas_lights",
            () -> new ChristmasLightsBlock(Keys.blockProps().mapColor(MapColor.COLOR_RED).noCollision()
                    .strength(0.2F).sound(SoundType.LANTERN).lightLevel(s -> 11).pushReaction(PushReaction.DESTROY)));
    public static final Supplier<WallDecorationBlock> WREATH = register("wreath",
            () -> new WallDecorationBlock(Keys.blockProps().mapColor(MapColor.PLANT).noCollision()
                    .strength(0.2F).sound(SoundType.AZALEA_LEAVES).pushReaction(PushReaction.DESTROY), 1.0));
    public static final Supplier<WallDecorationBlock> STOCKING = register("stocking",
            () -> new WallDecorationBlock(Keys.blockProps().mapColor(MapColor.COLOR_RED).noCollision()
                    .strength(0.2F).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY), 1.5));
    public static final Supplier<StarTopperBlock> STAR_TOPPER = register("star_topper",
            () -> new StarTopperBlock(Keys.blockProps().mapColor(MapColor.GOLD).noCollision()
                    .strength(0.3F).sound(SoundType.METAL).lightLevel(s -> 15).pushReaction(PushReaction.DESTROY)));
    public static final Supplier<OrnamentBlock> RED_ORNAMENT = ornament("red_ornament", MapColor.COLOR_RED);
    public static final Supplier<OrnamentBlock> GOLD_ORNAMENT = ornament("gold_ornament", MapColor.GOLD);
    public static final Supplier<OrnamentBlock> BLUE_ORNAMENT = ornament("blue_ornament", MapColor.COLOR_BLUE);
    public static final Supplier<AdventCalendarBlock> ADVENT_CALENDAR = register("advent_calendar",
            () -> new AdventCalendarBlock(Keys.blockProps().mapColor(MapColor.WOOD).strength(1.5F)
                    .sound(SoundType.WOOD).ignitedByLava()));

    // --- Presents
    public static final Supplier<PresentBlock> RED_PRESENT = present("red_present", MapColor.COLOR_RED);
    public static final Supplier<PresentBlock> GREEN_PRESENT = present("green_present", MapColor.COLOR_GREEN);
    public static final Supplier<PresentBlock> BLUE_PRESENT = present("blue_present", MapColor.COLOR_BLUE);
    public static final Supplier<PresentBlock> GOLD_PRESENT = present("gold_present", MapColor.GOLD);

    // --- Plants
    public static final Supplier<GingerCropBlock> GINGER_CROP = register("ginger_crop",
            () -> new GingerCropBlock(Keys.blockProps().mapColor(MapColor.PLANT).noCollision()
                    .randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY)));
    public static final Supplier<WildGingerBlock> WILD_GINGER = register("wild_ginger",
            () -> new WildGingerBlock(Keys.blockProps().mapColor(MapColor.PLANT).noCollision()
                    .instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)));

    private ModBlocks() {
    }

    private static BlockBehaviour.Properties gingerbread() {
        return Keys.blockProps().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.2F, 3.0F)
                .sound(SoundType.WOOD);
    }

    private static BlockBehaviour.Properties candy() {
        return Keys.blockProps().mapColor(MapColor.COLOR_RED).strength(1.0F, 3.0F)
                .sound(SoundType.CALCITE);
    }

    private static Supplier<OrnamentBlock> ornament(String name, MapColor color) {
        return register(name, () -> new OrnamentBlock(Keys.blockProps().mapColor(color).noCollision()
                .strength(0.1F).sound(SoundType.GLASS).pushReaction(PushReaction.DESTROY)));
    }

    private static Supplier<PresentBlock> present(String name, MapColor color) {
        Supplier<PresentBlock> block = register(name, () -> new PresentBlock(Keys.blockProps()
                .mapColor(color).strength(0.4F).sound(SoundType.WOOL).noOcclusion()));
        PRESENTS.add(block);
        return block;
    }

    private static <T extends Block> Supplier<T> register(String name, Supplier<T> factory) {
        Supplier<T> block = Services.PLATFORM.register(BuiltInRegistries.BLOCK, name, Keys.building(name, factory));
        NAMES.put(block, name);
        return block;
    }

    /** Registry path of a block declared here; safe before Forge has populated its registry objects. */
    public static String nameOf(Supplier<? extends Block> block) {
        String name = NAMES.get(block);
        if (name == null) {
            throw new IllegalArgumentException("Unknown block supplier " + block);
        }
        return name;
    }

    public static void init() {
    }
}
