package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import static com.EvgenWarGold.GregTechNightmare.GregTech.Mana.AEManaStackType.MANA_STACK_TYPE;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.tileentity.TileEntity;

import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.storage.MEInventoryHandler;
import appeng.parts.misc.PartStorageBus;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import vazkii.botania.api.mana.IManaPool;

/**
 * Mana side of a fluid storage bus. The bus itself stays a fluid bus; when it faces a Botania mana pool, this link adds
 * the pool to the network as mana storage next to the (empty) fluid storage. Keeping the two separate means filters,
 * partitioning and neighbour changes of the bus never mix mana with fluids.
 */
public class GTN_ManaStorageLink {

    /** How often pools are checked for mana changed by Botania itself. */
    private static final int POLL_INTERVAL = 5;
    private static final Map<GTN_ManaStorageLink, Boolean> LINKS = new WeakHashMap<>();

    private final PartStorageBus bus;
    private TileEntity target;
    private ManaPoolInventory inventory;
    private MEInventoryHandler<AEManaStack> handler;

    public GTN_ManaStorageLink(PartStorageBus bus) {
        this.bus = bus;
        synchronized (LINKS) {
            LINKS.put(this, Boolean.TRUE);
        }
    }

    /** Cell array of the bus for the mana stack type. */
    @SuppressWarnings("rawtypes")
    public List<IMEInventoryHandler> getCellArray() {
        if (!this.bus.getProxy()
            .isActive()) return Collections.emptyList();
        this.updateTarget();
        if (this.handler == null) return Collections.emptyList();
        this.handler.setPriority(this.bus.getPriority());
        return Collections.singletonList(this.handler);
    }

    /** Called when the block in front of the bus changes. */
    public void onNeighborChanged() {
        if (this.updateTarget()) this.postCellArrayUpdate();
    }

    /** @return true if the bus switched to another pool (or lost/found one) */
    private boolean updateTarget() {
        TileEntity facing = GTN_ManaBuses.getFacingTile(this.bus);
        if (!GTN_ManaBuses.isManaStorage(facing)) facing = null;
        if (facing == this.target && (facing == null || !facing.isInvalid())) return false;

        this.target = facing;
        if (facing == null) {
            this.inventory = null;
            this.handler = null;
        } else {
            this.inventory = new ManaPoolInventory((IManaPool) facing);
            this.handler = new MEInventoryHandler<>(this.inventory, MANA_STACK_TYPE);
        }
        return true;
    }

    private void postCellArrayUpdate() {
        try {
            this.bus.getProxy()
                .getGrid()
                .postEvent(new MENetworkCellArrayUpdate());
        } catch (GridAccessException ignored) {}
    }

    /** Posts mana changed by Botania itself to the network. */
    private void poll() {
        AENetworkProxy proxy = this.bus.getProxy();
        if (!proxy.isActive()) return;
        // the pool may not exist yet when the bus joins the network (world loading), look for it again
        if (this.inventory == null || ((TileEntity) this.inventory.getPool()).isInvalid()) {
            this.onNeighborChanged();
            return;
        }
        long change = this.inventory.pollChange();
        if (change == 0) return;
        ManaList changes = new ManaList();
        changes.add(new AEManaStack(change));
        try {
            proxy.getStorage()
                .postAlterationOfStoredItems(MANA_STACK_TYPE, changes, new MachineSource(this.bus));
        } catch (GridAccessException ignored) {}
    }

    private boolean isRemoved() {
        TileEntity host = this.bus.getHost() == null ? null
            : this.bus.getHost()
                .getTile();
        return host == null || host.isInvalid();
    }

    /** Server tick handler that polls all links. */
    public static class Ticker {

        private int timer;

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || ++this.timer < POLL_INTERVAL) return;
            this.timer = 0;
            GTN_ManaStorageLink[] links;
            synchronized (LINKS) {
                links = LINKS.keySet()
                    .toArray(new GTN_ManaStorageLink[0]);
            }
            for (GTN_ManaStorageLink link : links) {
                if (link.isRemoved()) {
                    synchronized (LINKS) {
                        LINKS.remove(link);
                    }
                } else {
                    link.poll();
                }
            }
        }
    }
}
