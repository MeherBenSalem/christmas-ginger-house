package tn.nightbeam.christmasgingerhouse.item;

/** Item that burns in furnaces. Loaders read {@link #burnTime()} through their fuel registries. */
public class FuelItem extends TooltipItem {

    private final int burnTime;

    public FuelItem(Properties properties, int burnTime) {
        super(properties);
        this.burnTime = burnTime;
    }

    public int burnTime() {
        return burnTime;
    }
}
