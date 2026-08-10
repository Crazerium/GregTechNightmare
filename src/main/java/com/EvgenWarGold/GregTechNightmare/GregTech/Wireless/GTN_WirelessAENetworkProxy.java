package com.EvgenWarGold.GregTechNightmare.GregTech.Wireless;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import appeng.api.AEApi;
import appeng.api.exceptions.FailedConnection;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridNode;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;

/**
 * Real AE bridge node used by a GregTech wireless endpoint.
 *
 * <p>The node is linked internally to the selected hub and can also expose the normal cable sides of its GregTech
 * host. It therefore acts as a real remote extension point of the hub grid: the host itself consumes one channel,
 * while devices connected behind it consume their own normal AE channels. DENSE_CAPACITY lets the invisible hub
 * connection carry a full 32-channel branch.</p>
 */
public class GTN_WirelessAENetworkProxy extends AENetworkProxy {

    private final GTN_WirelessMELink link;
    private IGridConnection wirelessConnection;
    private IGridNode connectedHubNode;

    public GTN_WirelessAENetworkProxy(IGridProxyable host, String nbtName, ItemStack visual, GTN_WirelessMELink link) {
        super(host, nbtName, visual, true);
        this.link = link;
        setFlags(GridFlags.REQUIRE_CHANNEL, GridFlags.DENSE_CAPACITY);
        setIdlePowerUsage(0.0);
        setValidSides(EnumSet.noneOf(ForgeDirection.class));
    }


    /**
     * Mirrors the normal GT hatch cable-side policy onto the wireless grid node.
     *
     * @param allSides whether the hatch has its additional-connection mode enabled
     * @param frontFacing the hatch front side used when additional connections are disabled
     */
    public void configureCableSides(boolean allSides, ForgeDirection frontFacing) {
        setValidSides(
            allSides ? EnumSet.complementOf(EnumSet.of(ForgeDirection.UNKNOWN)) : EnumSet.of(frontFacing));
    }

    /**
     * Ensures this endpoint has exactly one internal connection to its selected hub.
     *
     * @return true while a live internal connection exists
     */
    public boolean updateWirelessConnection() {
        if (!link.isLinked()) {
            destroyWirelessConnection();
            return false;
        }

        if (!isReady()) onReady();

        IGridNode localNode = getNode();
        AENetworkProxy hubProxy = link.getProxy();
        if (hubProxy != null && !hubProxy.isReady()) hubProxy.onReady();
        IGridNode hubNode = hubProxy == null ? null : hubProxy.getNode();

        if (localNode == null || hubNode == null || localNode == hubNode) {
            destroyWirelessConnection();
            return false;
        }

        if (wirelessConnection != null && connectedHubNode == hubNode && hasConnection(localNode, wirelessConnection)) {
            return true;
        }

        destroyWirelessConnection();
        try {
            wirelessConnection = AEApi.instance().createGridConnection(localNode, hubNode);
            connectedHubNode = hubNode;

            // Force pathing/channel state to notice the new invisible connection immediately.
            localNode.updateState();
            hubNode.updateState();
            return true;
        } catch (FailedConnection ignored) {
            wirelessConnection = null;
            connectedHubNode = null;
            return false;
        }
    }

    public void destroyWirelessConnection() {
        if (wirelessConnection != null) {
            try {
                wirelessConnection.destroy();
            } catch (RuntimeException ignored) {
                // The grid can already have destroyed the connection while unloading either endpoint.
            }
        }
        wirelessConnection = null;
        connectedHubNode = null;
    }

    @Override
    public void onChunkUnload() {
        destroyWirelessConnection();
        super.onChunkUnload();
    }

    @Override
    public void invalidate() {
        destroyWirelessConnection();
        super.invalidate();
    }

    private static boolean hasConnection(IGridNode node, IGridConnection expected) {
        for (IGridConnection connection : node.getConnections()) {
            if (connection == expected) return true;
        }
        return false;
    }
}
