package tn.nightbeam.christmasgingerhouse.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import tn.nightbeam.christmasgingerhouse.Constants;
import tn.nightbeam.christmasgingerhouse.item.CandyCaneSwordItem;
import tn.nightbeam.christmasgingerhouse.item.CandyTier;
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
            () -> new BlockItem(ModBlocks.GINGER_CROP.get(), Keys.itemProps().useItemDescriptionPrefix()));
    public static final Supplier<Item> GINGERBREAD_DOUGH = register("gingerbread_dough",
            () -> new Item(Keys.itemProps().food(food(2, 0.2F))));
    public static final Supplier<Item> GINGERBREAD = register("gingerbread",
            () -> new Item(Keys.itemProps().food(food(5, 0.6F))));
    public static final Supplier<Item> GINGERBREAD_COOKIE = register("gingerbread_cookie",
            () -> new TooltipItem(Keys.itemProps().food(food(6, 0.8F), Consumables.defaultFood().consumeSeconds(0.8F)
                    .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.ABSORPTION, 20 * 20, 0))).build())));
    public static final Supplier<Item> ICING = register("icing", () -> new Item(Keys.itemProps()));
    public static final Supplier<Item> CANDY_CANE = register("candy_cane",
            () -> new TooltipItem(Keys.itemProps().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).alwaysEdible().build(),
                    Consumables.defaultFood().consumeSeconds(0.8F)
                            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SPEED, 20 * 10, 0))).build())));
    public static final Supplier<Item> HOT_COCOA = register("hot_cocoa",
            () -> new HotCocoaItem(Keys.itemProps().stacksTo(16).usingConvertsTo(Items.BOWL)
                    .food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.6F).alwaysEdible().build(),
                            Consumables.defaultDrink()
                                    .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 6, 0))).build())));
    public static final Supplier<Item> NAUGHTY_COAL = register("naughty_coal",
            () -> new FuelItem(Keys.itemProps(), 3200));

    // --- Gear
    public static final Supplier<Item> CANDY_CANE_SWORD = register("candy_cane_sword",
            () -> new CandyCaneSwordItem(Keys.itemProps().sword(CandyTier.MATERIAL, 3.0F, -2.4F)));
    public static final Supplier<Item> SLEIGH_BELLS = register("sleigh_bells",
            () -> new SleighBellsItem(Keys.itemProps().stacksTo(1)));
    public static final Supplier<Item> SANTA_HAT = register("santa_hat",
            () -> new Item(Keys.itemProps().humanoidArmor(FestiveArmorMaterial.SANTA_HAT, ArmorType.HELMET)));
    public static final Supplier<Item> FESTIVE_SWEATER = register("festive_sweater",
            () -> new Item(Keys.itemProps().humanoidArmor(FestiveArmorMaterial.FESTIVE, ArmorType.CHESTPLATE)));
    public static final Supplier<Item> MUSIC_DISC_JINGLE_BELLS = register("music_disc_jingle_bells",
            () -> new Item(Keys.itemProps().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(
                    ResourceKey.create(Registries.JUKEBOX_SONG, Constants.id("jingle_bells")))));

    // --- Blocks
    public static final Supplier<Item> GINGERBREAD_BLOCK = block(ModBlocks.GINGERBREAD_BLOCK);
    public static final Supplier<Item> GINGERBREAD_BRICKS = block(ModBlocks.GINGERBREAD_BRICKS);
    public static final Supplier<Item> GINGERBREAD_BRICK_STAIRS = block(ModBlocks.GINGERBREAD_BRICK_STAIRS);
    public static final Supplier<Item> GINGERBREAD_BRICK_SLAB = block(ModBlocks.GINGERBREAD_BRICK_SLAB);
    public static final Supplier<Item> GINGERBREAD_BRICK_WALL = block(ModBlocks.GINGERBREAD_BRICK_WALL);
    public static final Supplier<Item> FROSTED_GINGERBREAD_BRICKS = block(ModBlocks.FROSTED_GINGERBREAD_BRICKS);
    public static final Supplier<Item> GINGERBREAD_DOOR = register("gingerbread_door",
            () -> new DoubleHighBlockItem(ModBlocks.GINGERBREAD_DOOR.get(), Keys.itemProps().useBlockDescriptionPrefix()));
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
            () -> new SpawnEggItem(Keys.itemProps().spawnEgg(ModEntities.GINGERBREAD_MAN.get())));
    public static final Supplier<Item> REINDEER_SPAWN_EGG = register("reindeer_spawn_egg",
            () -> new SpawnEggItem(Keys.itemProps().spawnEgg(ModEntities.REINDEER.get())));
    public static final Supplier<Item> KRAMPUS_SPAWN_EGG = register("krampus_spawn_egg",
            () -> new SpawnEggItem(Keys.itemProps().spawnEgg(ModEntities.KRAMPUS.get())));

    private ModItems() {
    }

    private static FoodProperties food(int nutrition, float saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build();
    }

    private static <T extends Item> Supplier<T> register(String name, Supplier<T> factory) {
        Supplier<T> item = Services.PLATFORM.register(BuiltInRegistries.ITEM, name, Keys.building(name, factory));
        TAB_ORDER.add(item);
        return item;
    }

    private static Supplier<Item> block(Supplier<? extends Block> block) {
        return register(ModBlocks.nameOf(block), () -> new BlockItem(block.get(), Keys.itemProps().useBlockDescriptionPrefix()));
    }

    private static Supplier<Item> present(Supplier<? extends Block> block) {
        return register(ModBlocks.nameOf(block), () -> new PresentItem(block.get(), Keys.itemProps().stacksTo(1).useBlockDescriptionPrefix()));
    }

    public static void init() {
    }
}
