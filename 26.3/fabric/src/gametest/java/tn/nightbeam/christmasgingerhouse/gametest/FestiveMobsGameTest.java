package tn.nightbeam.christmasgingerhouse.gametest;

import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import tn.nightbeam.christmasgingerhouse.registry.ModEntities;
import tn.nightbeam.christmasgingerhouse.registry.ModItems;

/**
 * Regression test for the 2.0.0 "Can't find attribute minecraft:tempt_range" ticking-entity crash.
 * On 26.x, TemptGoal.canUse() reads Attributes.TEMPT_RANGE, so every mob with a TemptGoal must
 * register that attribute. Spawns all three mod mobs next to a player holding their tempt food
 * and lets their AI run.
 */
public class FestiveMobsGameTest {

    private static final int AI_TICKS = 100;

    @GameTest(maxTicks = AI_TICKS + 40)
    public void festiveMobsTickNextToATemptingPlayer(GameTestHelper helper) {
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                helper.setBlock(x, 0, z, Blocks.SNOW_BLOCK);
            }
        }

        // Carrot tempts the reindeer, icing tempts the gingerbread man (TemptGoal checks both hands).
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.snapTo(helper.absoluteVec(new Vec3(4.5, 1.0, 4.5)));
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CARROT));
        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ModItems.ICING.get()));

        Mob reindeer = helper.spawn(ModEntities.REINDEER.get(), new Vec3(1.5, 1.0, 1.5));
        Mob gingerbreadMan = helper.spawn(ModEntities.GINGERBREAD_MAN.get(), new Vec3(6.5, 1.0, 1.5));
        Mob krampus = helper.spawn(ModEntities.KRAMPUS.get(), new Vec3(1.5, 1.0, 6.5));

        // The exact read TemptGoal.canUse() performs. Without the attribute this throws
        // IllegalArgumentException("Can't find attribute minecraft:tempt_range").
        for (Mob tempted : List.of(reindeer, gingerbreadMan)) {
            try {
                double range = tempted.getAttributeValue(Attributes.TEMPT_RANGE);
                helper.assertTrue(range > 0.0, tempted.getType().toShortString() + " has tempt range " + range);
            } catch (IllegalArgumentException e) {
                helper.fail(tempted.getType().toShortString() + ": " + e.getMessage());
                return;
            }
        }

        // Let the real goal selectors run (TemptGoal, PanicGoal, MeleeAttackGoal, target goals...).
        // A missing attribute here crashes the server tick, which fails the whole game test run.
        helper.runAfterDelay(AI_TICKS, () -> {
            for (Mob mob : List.of(reindeer, gingerbreadMan, krampus)) {
                helper.assertTrue(mob.isAlive() && !mob.isRemoved(), mob.getType().toShortString() + " survived " + AI_TICKS + " ticks");
            }
            helper.succeed();
        });
    }
}
