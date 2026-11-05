package tn.nightbeam.christmasgingerhouse.util;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;
import tn.nightbeam.christmasgingerhouse.Constants;

/**
 * Awards advancements whose only criterion is {@code minecraft:impossible} and named after
 * the advancement. Used for actions vanilla triggers cannot see (unwrapping, advent doors).
 */
public final class Advancements {

    private Advancements() {
    }

    public static void award(ServerPlayer player, String name) {
        AdvancementHolder advancement = player.server.getAdvancements().get(Constants.id(name));
        if (advancement != null) {
            player.getAdvancements().award(advancement, name);
        }
    }
}
