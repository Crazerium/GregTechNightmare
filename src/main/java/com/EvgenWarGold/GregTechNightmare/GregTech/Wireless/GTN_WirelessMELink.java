package com.EvgenWarGold.GregTechNightmare.GregTech.Wireless;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_WirelessMEIOHub;

import appeng.me.helpers.AENetworkProxy;

public final class GTN_WirelessMELink {

    private static final String TAG_NETWORK_ID = "GTNWirelessNetworkId";
    private static final String TAG_NETWORK_NAME = "GTNWirelessNetworkName";
    private static final int ONLINE_STABILIZATION_TICKS = 20;

    private UUID networkId;
    private String networkName = "";
    private int consecutiveOnlineTicks;
    private boolean ready;

    public boolean bind(ItemStack dataStick) {
        UUID id = GTN_WirelessMEDataStick.getNetworkId(dataStick);
        if (id == null) return false;

        networkId = id;
        networkName = GTN_WirelessMEDataStick.getNetworkName(dataStick);
        resetRuntimeState();
        return true;
    }

    public void disconnect() {
        networkId = null;
        networkName = "";
        resetRuntimeState();
    }

    /**
     * Updates the stable usable state from the endpoint's real AE node. The endpoint must have an allocated channel,
     * power and a completed grid boot before the wireless link is considered ready.
     *
     * @return true when the stable ready state changed
     */
    public boolean tick(AENetworkProxy endpointProxy) {
        boolean wasReady = ready;
        boolean online = endpointProxy != null && endpointProxy.isActive();

        if (online) {
            if (consecutiveOnlineTicks < ONLINE_STABILIZATION_TICKS) consecutiveOnlineTicks++;
        } else {
            consecutiveOnlineTicks = 0;
        }

        ready = online && consecutiveOnlineTicks >= ONLINE_STABILIZATION_TICKS;
        return wasReady != ready;
    }

    public AENetworkProxy getProxy() {
        GTN_WirelessMEIOHub hub = getHub();
        return hub == null ? null : hub.getProxy();
    }

    public GTN_WirelessMEIOHub getHub() {
        return GTN_WirelessMENetworkRegistry.get(networkId);
    }

    public boolean isRemoteOnline() {
        AENetworkProxy proxy = getProxy();
        return proxy != null && proxy.isActive();
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isLinked() {
        return networkId != null;
    }

    public String getNetworkName() {
        if (!networkName.isEmpty()) return networkName;
        return networkId == null ? "None" : networkId.toString();
    }

    public UUID getNetworkId() {
        return networkId;
    }

    public void save(NBTTagCompound tag) {
        if (networkId == null) return;

        tag.setString(TAG_NETWORK_ID, networkId.toString());
        tag.setString(TAG_NETWORK_NAME, networkName);
    }

    public void load(NBTTagCompound tag) {
        disconnect();
        if (!tag.hasKey(TAG_NETWORK_ID)) return;

        try {
            networkId = UUID.fromString(tag.getString(TAG_NETWORK_ID));
            networkName = tag.getString(TAG_NETWORK_NAME);
            resetRuntimeState();
        } catch (IllegalArgumentException ignored) {
            disconnect();
        }
    }

    private void resetRuntimeState() {
        consecutiveOnlineTicks = 0;
        ready = false;
    }
}
