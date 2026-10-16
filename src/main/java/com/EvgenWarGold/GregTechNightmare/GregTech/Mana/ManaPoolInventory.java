package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStackType.MANA_STACK_TYPE;

import javax.annotation.Nonnull;

import net.minecraft.tileentity.TileEntity;

import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEStackType;
import appeng.api.storage.data.IItemList;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.common.block.tile.mana.TilePool;

/**
 * A Botania mana pool seen through a storage bus. The pool also changes without AE2 knowing (spreaders, sparks,
 * items), {@link #pollChange()} reports those changes so they can be posted to the network.
 */
public class ManaPoolInventory implements IMEInventory<AEManaStack> {

    /** Shown amount of a creative pool: it never runs out, so any order has to fit. */
    public static final long CREATIVE_MANA = 1_000_000_000_000L;

    private final IManaPool pool;
    private final boolean creative;
    /** Mana the network currently knows about. */
    private long knownMana;

    public ManaPoolInventory(IManaPool pool) {
        this.pool = pool;
        this.creative = isCreativePool(pool);
        this.knownMana = this.readMana();
    }

    public IManaPool getPool() {
        return this.pool;
    }

    /** The Botania creative pool is a mana pool with metadata 1. */
    private static boolean isCreativePool(IManaPool pool) {
        TileEntity tile = (TileEntity) pool;
        return tile instanceof TilePool && tile.getWorldObj() != null && tile.getBlockMetadata() == 1;
    }

    private long readMana() {
        if (this.creative) return ((TileEntity) this.pool).isInvalid() ? 0 : CREATIVE_MANA;
        return ((TileEntity) this.pool).isInvalid() ? 0 : Math.max(0, this.pool.getCurrentMana());
    }

    /** @return mana added (positive) or removed (negative) since the network last saw the pool */
    public long pollChange() {
        long mana = this.readMana();
        long change = mana - this.knownMana;
        this.knownMana = mana;
        return change;
    }

    @Override
    public AEManaStack injectItems(AEManaStack input, Actionable action, BaseActionSource source) {
        if (input == null) return null;
        // mana sent into a creative pool would just vanish
        if (this.creative) return input;
        long space = GTN_ManaBuses.getFreeSpace((TileEntity) this.pool);
        if (space <= 0 || this.pool.isFull()) return input;
        long accepted = Math.min(Math.min(space, Integer.MAX_VALUE), input.getStackSize());
        if (action == Actionable.MODULATE) {
            this.pool.recieveMana((int) accepted);
            // the network posts this change itself
            this.knownMana = this.readMana();
        }
        if (accepted >= input.getStackSize()) return null;
        AEManaStack leftover = input.copy();
        leftover.decStackSize(accepted);
        return leftover;
    }

    @Override
    public AEManaStack extractItems(AEManaStack request, Actionable action, BaseActionSource source) {
        if (request == null) return null;
        long extracted = this.creative ? request.getStackSize()
            : Math.min(Math.min(this.readMana(), Integer.MAX_VALUE), request.getStackSize());
        if (extracted <= 0) return null;
        if (action == Actionable.MODULATE && !this.creative) {
            this.pool.recieveMana((int) -extracted);
            this.knownMana = this.readMana();
        }
        return new AEManaStack(extracted);
    }

    @Override
    public IItemList<AEManaStack> getAvailableItems(IItemList<AEManaStack> out, int iteration) {
        long mana = this.readMana();
        this.knownMana = mana;
        if (mana > 0) out.addStorage(new AEManaStack(mana));
        return out;
    }

    @Override
    public AEManaStack getAvailableItem(@Nonnull AEManaStack request, int iteration) {
        long mana = this.readMana();
        return mana > 0 ? new AEManaStack(mana) : null;
    }

    @Override
    public StorageChannel getChannel() {
        return null;
    }

    @Nonnull
    @Override
    public IAEStackType<?> getStackType() {
        return MANA_STACK_TYPE;
    }
}
