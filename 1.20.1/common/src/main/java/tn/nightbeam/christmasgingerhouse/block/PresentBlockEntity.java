package tn.nightbeam.christmasgingerhouse.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;
import tn.nightbeam.christmasgingerhouse.registry.ModBlockEntities;

/**
 * Nine slots of gift. A present is either sealed with a loot table (generated in structures,
 * dropped by Krampus, opened from the advent calendar) or filled by hand by a player, whose
 * name is written on the tag.
 */
public class PresentBlockEntity extends RandomizableContainerBlockEntity {

    public static final int SIZE = 9;
    public static final String TAG_SENDER = "Sender";

    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    @Nullable
    private String sender;

    public PresentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESENT.get(), pos, state);
    }

    public boolean isSealedWithLoot() {
        return lootTable != null;
    }

    public boolean hasGift() {
        return isSealedWithLoot() || !isEmpty();
    }

    @Nullable
    public String getSender() {
        return sender;
    }

    @Override
    public void stopOpen(Player player) {
        super.stopOpen(player);
        if (!player.isSpectator()) {
            sender = isEmpty() ? null : player.getGameProfile().getName();
            setChanged();
        }
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.christmas_ginger_house.present");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new DispenserMenu(containerId, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, items);
        }
        sender = tag.contains(TAG_SENDER) ? tag.getString(TAG_SENDER) : null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, items, false);
        }
        if (sender != null) {
            tag.putString(TAG_SENDER, sender);
        }
    }
}
