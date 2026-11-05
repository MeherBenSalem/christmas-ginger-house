package tn.nightbeam.christmasgingerhouse.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import tn.nightbeam.christmasgingerhouse.Constants;

public final class ModModelLayers {

    public static final ModelLayerLocation GINGERBREAD_MAN = layer("gingerbread_man");
    public static final ModelLayerLocation REINDEER = layer("reindeer");
    public static final ModelLayerLocation KRAMPUS = layer("krampus");
    public static final ModelLayerLocation SANTA_HAT = layer("santa_hat");

    private ModModelLayers() {
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(Constants.id(name), "main");
    }
}
