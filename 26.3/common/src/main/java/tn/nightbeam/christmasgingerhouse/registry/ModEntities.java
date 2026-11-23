package tn.nightbeam.christmasgingerhouse.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import tn.nightbeam.christmasgingerhouse.Constants;
import tn.nightbeam.christmasgingerhouse.entity.GingerbreadMan;
import tn.nightbeam.christmasgingerhouse.entity.Krampus;
import tn.nightbeam.christmasgingerhouse.entity.Reindeer;
import tn.nightbeam.christmasgingerhouse.platform.Services;

import java.util.function.Supplier;

public final class ModEntities {

    public static final Supplier<EntityType<GingerbreadMan>> GINGERBREAD_MAN = register("gingerbread_man",
            EntityType.Builder.of(GingerbreadMan::new, MobCategory.CREATURE).sized(0.5F, 1.0F).clientTrackingRange(8));
    public static final Supplier<EntityType<Reindeer>> REINDEER = register("reindeer",
            EntityType.Builder.of(Reindeer::new, MobCategory.CREATURE).sized(0.9F, 1.5F).clientTrackingRange(10));
    public static final Supplier<EntityType<Krampus>> KRAMPUS = register("krampus",
            EntityType.Builder.of(Krampus::new, MobCategory.MONSTER).sized(0.8F, 2.4F).clientTrackingRange(10));

    private ModEntities() {
    }

    private static <T extends net.minecraft.world.entity.Entity> Supplier<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return Services.PLATFORM.register(BuiltInRegistries.ENTITY_TYPE, name,
                () -> builder.build(Keys.entity(name)));
    }

    public static void init() {
    }
}
