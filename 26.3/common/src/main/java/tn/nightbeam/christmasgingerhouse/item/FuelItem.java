package tn.nightbeam.christmasgingerhouse.item;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import tn.nightbeam.christmasgingerhouse.Constants;

/**
 * Item that burns in furnaces. On 26.3 burn time is the vanilla {@code minecraft:cooking_fuel} item
 * component, so {@link #withBurnTime} puts it on the item properties (read through the component's own
 * codec, which needs both {@code burn_time} and {@code speed_multiplier}). NeoForge 26.3 has no furnace_fuels
 * data map any more, so the component is the only source of the burn time on both loaders.
 */
public class FuelItem extends TooltipItem {

    private final int burnTime;

    public FuelItem(Properties properties, int burnTime) {
        super(withBurnTime(properties, burnTime));
        this.burnTime = burnTime;
    }

    public int burnTime() {
        return burnTime;
    }

    private static Item.Properties withBurnTime(Item.Properties properties, int burnTime) {
        JsonObject json = new JsonObject();
        json.addProperty("burn_time", burnTime);
        // Normal furnace speed; without this field the codec rejects the component and the item does not burn.
        json.addProperty("speed_multiplier", 1.0F);
        DataComponents.COOKING_FUEL.codecOrThrow().parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> Constants.LOG.warn("Could not set the furnace burn time of a fuel item: {}", error))
                .ifPresent(fuel -> properties.component(DataComponents.COOKING_FUEL, fuel));
        return properties;
    }
}
