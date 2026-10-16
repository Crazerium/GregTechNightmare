package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStackType.MANA_STACK_TYPE;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

import com.EvgenWarGold.GregTechNightmare.GregTech.Blocks.GTN_Blocks;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEMonitor;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkTile;
import appeng.util.IterationCounter;
import io.netty.buffer.ByteBuf;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;

/**
 * Mana pool without its own storage: Botania sees the mana of the whole ME network. Spreaders, sparks and buses fill
 * the network through it, sparks take mana from it. The capacity is the free space of the mana cells in the network.
 * <p>
 * The client has no access to the network, so the amount and the capacity are synced to it for spark particles and the
 * Wand of the Forest HUD.
 */
public class TileFluxManaPool extends AENetworkTile implements IManaPool, ISparkAttachable {

    private static final int SYNC_INTERVAL = 10;
    /** Simulated insert used to measure the free space of the network. */
    private static final long SPACE_PROBE = Long.MAX_VALUE / 4;

    private final MachineSource source = new MachineSource(this);

    /** Network state cached for one tick: Botania asks for it many times per tick. */
    private long cachedTick = -1;
    private long cachedMana;
    private long cachedSpace;

    /** Last values sent to the client (server) / received from the server (client). */
    private long syncedMana;
    private long syncedCapacity;
    private int syncTimer;

    public TileFluxManaPool() {
        this.getProxy()
            .setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getProxy()
            .setIdlePowerUsage(1.0);
    }

    @Override
    protected AENetworkProxy createProxy() {
        return new AENetworkProxy(this, "proxy", new ItemStack(GTN_Blocks.FLUX_MANA_POOL), true);
    }

    @SuppressWarnings("unchecked")
    private IMEMonitor<AEManaStack> getMonitor() {
        if (this.worldObj == null || this.worldObj.isRemote
            || !this.getProxy()
                .isActive())
            return null;
        try {
            return (IMEMonitor<AEManaStack>) this.getProxy()
                .getStorage()
                .getMEMonitor(MANA_STACK_TYPE);
        } catch (GridAccessException e) {
            return null;
        }
    }

    private void updateCache() {
        long tick = this.worldObj == null ? 0 : this.worldObj.getTotalWorldTime();
        if (tick == this.cachedTick) return;
        this.cachedTick = tick;
        this.cachedMana = 0;
        this.cachedSpace = 0;

        IMEMonitor<AEManaStack> monitor = this.getMonitor();
        if (monitor == null) return;
        AEManaStack stored = monitor.getAvailableItem(new AEManaStack(), IterationCounter.fetchNewId());
        this.cachedMana = stored == null ? 0 : stored.getStackSize();
        AEManaStack notStored = monitor.injectItems(new AEManaStack(SPACE_PROBE), Actionable.SIMULATE, this.source);
        this.cachedSpace = SPACE_PROBE - (notStored == null ? 0 : notStored.getStackSize());
    }

    private void invalidateCache() {
        this.cachedTick = -1;
    }

    /** Mana in the network (synced value on the client). */
    public long getManaLong() {
        if (this.worldObj != null && this.worldObj.isRemote) return this.syncedMana;
        this.updateCache();
        return this.cachedMana;
    }

    /** Mana plus free space of the mana cells (synced value on the client). */
    public long getCapacityLong() {
        if (this.worldObj != null && this.worldObj.isRemote) return this.syncedCapacity;
        this.updateCache();
        return this.cachedMana + this.cachedSpace;
    }

    private long getSpaceLong() {
        return Math.max(0, this.getCapacityLong() - this.getManaLong());
    }

    // region Sync
    @TileEvent(TileEventType.TICK)
    public void tickFluxManaPool() {
        if (this.worldObj.isRemote || ++this.syncTimer < SYNC_INTERVAL) return;
        this.syncTimer = 0;
        long mana = this.getManaLong();
        long capacity = this.getCapacityLong();
        if (mana != this.syncedMana || capacity != this.syncedCapacity) {
            this.syncedMana = mana;
            this.syncedCapacity = capacity;
            this.markForUpdate();
        }
    }

    @TileEvent(TileEventType.NETWORK_WRITE)
    public void writeFluxManaPool(ByteBuf data) {
        data.writeLong(this.syncedMana);
        data.writeLong(this.syncedCapacity);
    }

    @TileEvent(TileEventType.NETWORK_READ)
    public boolean readFluxManaPool(ByteBuf data) {
        this.syncedMana = data.readLong();
        this.syncedCapacity = data.readLong();
        return false;
    }
    // endregion

    // region Botania
    @Override
    public int getCurrentMana() {
        return (int) Math.min(this.getManaLong(), Integer.MAX_VALUE);
    }

    @Override
    public boolean isFull() {
        return this.getSpaceLong() <= 0;
    }

    @Override
    public void recieveMana(int mana) {
        IMEMonitor<AEManaStack> monitor = this.getMonitor();
        if (monitor == null || mana == 0) return;
        if (mana > 0) monitor.injectItems(new AEManaStack(mana), Actionable.MODULATE, this.source);
        else monitor.extractItems(new AEManaStack(-(long) mana), Actionable.MODULATE, this.source);
        this.invalidateCache();
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return true;
    }

    @Override
    public boolean isOutputtingPower() {
        return false;
    }

    @Override
    public boolean canAttachSpark(ItemStack stack) {
        return true;
    }

    @Override
    public void attachSpark(ISparkEntity entity) {}

    @Override
    public ISparkEntity getAttachedSpark() {
        List<ISparkEntity> sparks = this.worldObj.getEntitiesWithinAABB(
            ISparkEntity.class,
            AxisAlignedBB.getBoundingBox(
                this.xCoord,
                this.yCoord + 1,
                this.zCoord,
                this.xCoord + 1,
                this.yCoord + 2,
                this.zCoord + 1));
        return sparks.size() == 1 ? sparks.get(0) : null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return false;
    }

    @Override
    public int getAvailableSpaceForMana() {
        return (int) Math.min(this.getSpaceLong(), Integer.MAX_VALUE);
    }
    // endregion
}
