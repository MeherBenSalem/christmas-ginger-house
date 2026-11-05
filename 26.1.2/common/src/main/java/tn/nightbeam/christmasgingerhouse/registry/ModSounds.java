package tn.nightbeam.christmasgingerhouse.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import tn.nightbeam.christmasgingerhouse.Constants;
import tn.nightbeam.christmasgingerhouse.platform.Services;

import java.util.function.Supplier;

public final class ModSounds {

    public static final Supplier<SoundEvent> SLEIGH_BELLS = sound("item.sleigh_bells.ring");
    public static final Supplier<SoundEvent> PRESENT_UNWRAP = sound("block.present.unwrap");
    public static final Supplier<SoundEvent> PRESENT_WRAP = sound("block.present.wrap");
    public static final Supplier<SoundEvent> ADVENT_CALENDAR_OPEN = sound("block.advent_calendar.open");
    public static final Supplier<SoundEvent> GINGERBREAD_MAN_AMBIENT = sound("entity.gingerbread_man.ambient");
    public static final Supplier<SoundEvent> GINGERBREAD_MAN_HURT = sound("entity.gingerbread_man.hurt");
    public static final Supplier<SoundEvent> GINGERBREAD_MAN_DEATH = sound("entity.gingerbread_man.death");
    public static final Supplier<SoundEvent> KRAMPUS_AMBIENT = sound("entity.krampus.ambient");
    public static final Supplier<SoundEvent> KRAMPUS_HURT = sound("entity.krampus.hurt");
    public static final Supplier<SoundEvent> KRAMPUS_DEATH = sound("entity.krampus.death");
    public static final Supplier<SoundEvent> KRAMPUS_CHAINS = sound("entity.krampus.chains");
    public static final Supplier<SoundEvent> MUSIC_DISC_JINGLE_BELLS = sound("music_disc.jingle_bells");

    private ModSounds() {
    }

    private static Supplier<SoundEvent> sound(String name) {
        return Services.PLATFORM.register(BuiltInRegistries.SOUND_EVENT, name,
                () -> SoundEvent.createVariableRangeEvent(Constants.id(name)));
    }

    public static void init() {
    }
}
