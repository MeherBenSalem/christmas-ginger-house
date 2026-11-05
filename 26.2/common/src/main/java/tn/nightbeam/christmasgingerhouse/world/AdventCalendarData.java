package tn.nightbeam.christmasgingerhouse.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import tn.nightbeam.christmasgingerhouse.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Per-player advent progress, saved with the overworld. Each player opens one door per
 * Minecraft day; after door 24 the calendar starts a new season the following day.
 */
public class AdventCalendarData extends SavedData {

    public static final int DOORS = 24;

    public record Progress(int doorsOpened, long lastDay) {
        static final Codec<Progress> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("doors").forGetter(Progress::doorsOpened),
                Codec.LONG.fieldOf("last_day").forGetter(Progress::lastDay)).apply(i, Progress::new));
    }

    /** Result of trying a door: {@code door} is 1..24 when opened, otherwise the next door number. */
    public record Attempt(boolean opened, int door) {
    }

    private static final Codec<AdventCalendarData> CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, Progress.CODEC)
            .xmap(AdventCalendarData::new, data -> data.players).fieldOf("players").codec();
    private static final SavedDataType<AdventCalendarData> TYPE = new SavedDataType<>(
            Constants.id("advent_calendar"), AdventCalendarData::new, CODEC, null);

    private final Map<UUID, Progress> players;

    public AdventCalendarData() {
        this(Map.of());
    }

    private AdventCalendarData(Map<UUID, Progress> players) {
        this.players = new HashMap<>(players);
    }

    public static AdventCalendarData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public Attempt tryOpen(UUID player, long day) {
        Progress progress = players.getOrDefault(player, new Progress(0, -1));
        if (progress.lastDay() == day) {
            return new Attempt(false, progress.doorsOpened() % DOORS + 1);
        }
        int door = progress.doorsOpened() % DOORS + 1;
        players.put(player, new Progress(door, day));
        setDirty();
        return new Attempt(true, door);
    }
}
