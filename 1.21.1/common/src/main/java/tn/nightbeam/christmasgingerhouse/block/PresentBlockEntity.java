package tn.nightbeam.christmasgingerhouse.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
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
import tn.nightbeam.christmasgingerhouse.registry.ModBlockEntities;
import tn.nightbeam.christmasgingerhouse.registry.ModDataComponents;

import org.jetbrains.annotations.Nullable;

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
        return getLootTable() != null;
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
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, items, registries);
        }
        sender = tag.contains(TAG_SENDER) ? tag.getString(TAG_SENDER) : null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, items, false, registries);
        }
        if (sender != null) {
            tag.putString(TAG_SENDER, sender);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        sender = input.get(ModDataComponents.SENDER.get());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        if (sender != null) {
            builder.set(ModDataComponents.SENDER.get(), sender);
        }
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(TAG_SENDER);
    }
}
