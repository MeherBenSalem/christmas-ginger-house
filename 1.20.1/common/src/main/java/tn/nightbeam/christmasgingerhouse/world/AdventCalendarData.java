package tn.nightbeam.christmasgingerhouse.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Per-player advent progress, saved with the overworld. Each player opens one door per
 * Minecraft day; after door 24 the calendar starts a new season the following day.
 */
public class AdventCalendarData extends SavedData {

    public static final int DOORS = 24;
    private static final String NAME = "christmas_ginger_house_advent";

    private final Map<UUID, Progress> players = new HashMap<>();

    public record Progress(int doorsOpened, long lastDay) {
    }

    /** Result of trying a door: {@code door} is 1..24 when opened, otherwise the next door number. */
    public record Attempt(boolean opened, int door) {
    }

    public static AdventCalendarData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(AdventCalendarData::load, AdventCalendarData::new, NAME);
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

    public static AdventCalendarData load(CompoundTag tag) {
        AdventCalendarData data = new AdventCalendarData();
        ListTag list = tag.getList("Players", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            data.players.put(entry.getUUID("Id"), new Progress(entry.getInt("Doors"), entry.getLong("LastDay")));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        players.forEach((id, progress) -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("Id", id);
            entry.putInt("Doors", progress.doorsOpened());
            entry.putLong("LastDay", progress.lastDay());
            list.add(entry);
        });
        tag.put("Players", list);
        return tag;
    }
}
