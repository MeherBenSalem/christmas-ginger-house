package tn.nightbeam.christmasgingerhouse.block;

import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/** An iced gingerbread door. Opens by hand like a wooden door. */
public class GingerbreadDoorBlock extends DoorBlock {

    public GingerbreadDoorBlock(Properties properties) {
        super(BlockSetType.OAK, properties);
    }
}
