package tn.nightbeam.christmasgingerhouse.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import tn.nightbeam.christmasgingerhouse.item.CandyCaneSwordItem;
import tn.nightbeam.christmasgingerhouse.item.FestiveArmorMaterial;
import tn.nightbeam.christmasgingerhouse.item.FuelItem;
import tn.nightbeam.christmasgingerhouse.item.HotCocoaItem;
import tn.nightbeam.christmasgingerhouse.item.PresentItem;
import tn.nightbeam.christmasgingerhouse.item.SleighBellsItem;
import tn.nightbeam.christmasgingerhouse.item.TooltipItem;
import tn.nightbeam.christmasgingerhouse.platform.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ModItems {

    /** Creative tab order. */
    public static final List<Supplier<? extends Item>> TAB_ORDER = new ArrayList<>();

    // --- Food & ingredients
    public static final Supplier<Item> GINGER_ROOT = register("ginger_root",
            () -> new ItemNameBlockItem(ModBlocks.GINGER_CROP.get(), new Item.Properties()));
    public static final Supplier<Item> GINGERBREAD_DOUGH = register("gingerbread_dough",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build())));
    public static final Supplier<Item> GINGERBREAD = register("gingerbread",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.6F).build())));
    public static final Supplier<Item> GINGERBREAD_COOKIE = register("gingerbread_cookie",
            () -> new TooltipItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.8F).fast()
                    .effect(new MobEffectInstance(MobEffects.ABSORPTION, 20 * 20, 0), 1.0F).build())));
    public static final Supplier<Item> ICING = register("icing", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CANDY_CANE = register("candy_cane",
            () -> new TooltipItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).fast()
                    .alwaysEat().effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 10, 0), 1.0F).build())));
    public static final Supplier<Item> HOT_COCOA = register("hot_cocoa",
            () -> new HotCocoaItem(new Item.Properties().stacksTo(16)
                    .food(new FoodProperties.Builder().nutrition(4).saturationMod(0.6F).alwaysEat()
                            .effect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 6, 0), 1.0F).build())));
    public static final Supplier<Item> NAUGHTY_COAL = register("naughty_coal",
            () -> new FuelItem(new Item.Properties(), 3200));

    // --- Gear
    public static final Supplier<Item> CANDY_CANE_SWORD = register("candy_cane_sword",
            () -> new CandyCaneSwordItem(new Item.Properties()));
    public static final Supplier<Item> SLEIGH_BELLS = register("sleigh_bells",
            () -> new SleighBellsItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> SANTA_HAT = register("santa_hat",
            () -> Services.PLATFORM.createFestiveArmor(FestiveArmorMaterial.FESTIVE, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final Supplier<Item> FESTIVE_SWEATER = register("festive_sweater",
            () -> Services.PLATFORM.createFestiveArmor(FestiveArmorMaterial.FESTIVE, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final Supplier<Item> MUSIC_DISC_JINGLE_BELLS = register("music_disc_jingle_bells",
            () -> Services.PLATFORM.createRecord(12, ModSounds.MUSIC_DISC_JINGLE_BELLS,
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 64));

    // --- Blocks
    public static final Supplier<Item> GINGERBREAD_BLOCK = block(ModBlocks.GINGERBREAD_BLOCK);
    public static final Supplier<Item> GINGERBREAD_BRICKS = block(ModBlocks.GINGERBREAD_BRICKS);
    public static final Supplier<Item> GINGERBREAD_BRICK_STAIRS = block(ModBlocks.GINGERBREAD_BRICK_STAIRS);
    public static final Supplier<Item> GINGERBREAD_BRICK_SLAB = block(ModBlocks.GINGERBREAD_BRICK_SLAB);
    public static final Supplier<Item> GINGERBREAD_BRICK_WALL = block(ModBlocks.GINGERBREAD_BRICK_WALL);
    public static final Supplier<Item> FROSTED_GINGERBREAD_BRICKS = block(ModBlocks.FROSTED_GINGERBREAD_BRICKS);
    public static final Supplier<Item> GINGERBREAD_DOOR = register("gingerbread_door",
            () -> new DoubleHighBlockItem(ModBlocks.GINGERBREAD_DOOR.get(), new Item.Properties()));
    public static final Supplier<Item> GINGERBREAD_TRAPDOOR = block(ModBlocks.GINGERBREAD_TRAPDOOR);
    public static final Supplier<Item> ICING_BLOCK = block(ModBlocks.ICING_BLOCK);
    public static final Supplier<Item> CANDY_CANE_BLOCK = block(ModBlocks.CANDY_CANE_BLOCK);
    public static final Supplier<Item> PEPPERMINT_BLOCK = block(ModBlocks.PEPPERMINT_BLOCK);
    public static final Supplier<Item> CHRISTMAS_LIGHTS = block(ModBlocks.CHRISTMAS_LIGHTS);
    public static final Supplier<Item> WREATH = block(ModBlocks.WREATH);
    public static final Supplier<Item> STOCKING = block(ModBlocks.STOCKING);
    public static final Supplier<Item> STAR_TOPPER = block(ModBlocks.STAR_TOPPER);
    public static final Supplier<Item> RED_ORNAMENT = block(ModBlocks.RED_ORNAMENT);
    public static final Supplier<Item> GOLD_ORNAMENT = block(ModBlocks.GOLD_ORNAMENT);
    public static final Supplier<Item> BLUE_ORNAMENT = block(ModBlocks.BLUE_ORNAMENT);
    public static final Supplier<Item> ADVENT_CALENDAR = block(ModBlocks.ADVENT_CALENDAR);
    public static final Supplier<Item> RED_PRESENT = present(ModBlocks.RED_PRESENT);
    public static final Supplier<Item> GREEN_PRESENT = present(ModBlocks.GREEN_PRESENT);
    public static final Supplier<Item> BLUE_PRESENT = present(ModBlocks.BLUE_PRESENT);
    public static final Supplier<Item> GOLD_PRESENT = present(ModBlocks.GOLD_PRESENT);
    public static final Supplier<Item> WILD_GINGER = block(ModBlocks.WILD_GINGER);

    // --- Spawn eggs
    public static final Supplier<Item> GINGERBREAD_MAN_SPAWN_EGG = register("gingerbread_man_spawn_egg",
            () -> Services.PLATFORM.createSpawnEgg(ModEntities.GINGERBREAD_MAN, 0xB5682C, 0xF7FBFF, new Item.Properties()));
    public static final Supplier<Item> REINDEER_SPAWN_EGG = register("reindeer_spawn_egg",
            () -> Services.PLATFORM.createSpawnEgg(ModEntities.REINDEER, 0x87593A, 0xE3CFB0, new Item.Properties()));
    public static final Supplier<Item> KRAMPUS_SPAWN_EGG = register("krampus_spawn_egg",
            () -> Services.PLATFORM.createSpawnEgg(ModEntities.KRAMPUS, 0x24160E, 0xCF2337, new Item.Properties()));

    private ModItems() {
    }

    private static <T extends Item> Supplier<T> register(String name, Supplier<T> factory) {
        Supplier<T> item = Services.PLATFORM.register(BuiltInRegistries.ITEM, name, factory);
        TAB_ORDER.add(item);
        return item;
    }

    private static Supplier<Item> block(Supplier<? extends Block> block) {
        return register(ModBlocks.nameOf(block), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static Supplier<Item> present(Supplier<? extends Block> block) {
        return register(ModBlocks.nameOf(block), () -> new PresentItem(block.get(), new Item.Properties().stacksTo(1)));
    }

    public static void init() {
    }
}
