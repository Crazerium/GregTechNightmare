package com.EvgenWarGold.GregTechNightmare.GregTech.Wireless;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_WirelessMEIOHub;

public final class GTN_WirelessMENetworkRegistry {

    private static final long ENTRY_TIMEOUT_NANOS = 10_000_000_000L;
    private static final Map<UUID, Entry> NETWORKS = new ConcurrentHashMap<>();

    private GTN_WirelessMENetworkRegistry() {}

    public static void register(GTN_WirelessMEIOHub hub) {
        UUID networkId = hub.getNetworkId();
        if (networkId == null) return;

        NETWORKS.put(networkId, new Entry(hub, System.nanoTime()));
    }

    public static void unregister(GTN_WirelessMEIOHub hub) {
        UUID networkId = hub.getNetworkId();
        if (networkId == null) return;

        NETWORKS.computeIfPresent(networkId, (id, entry) -> entry.getHub() == hub ? null : entry);
    }

    public static GTN_WirelessMEIOHub get(UUID networkId) {
        if (networkId == null) return null;

        Entry entry = NETWORKS.get(networkId);
        if (entry == null) return null;

        if (System.nanoTime() - entry.lastSeenNanos > ENTRY_TIMEOUT_NANOS) {
            NETWORKS.remove(networkId, entry);
            return null;
        }

        GTN_WirelessMEIOHub hub = entry.getHub();
        if (hub == null || hub.getBaseMetaTileEntity() == null || hub.getBaseMetaTileEntity().getWorld() == null) {
            NETWORKS.remove(networkId, entry);
            return null;
        }

        return hub;
    }

    private static final class Entry {

        private final WeakReference<GTN_WirelessMEIOHub> hub;
        private final long lastSeenNanos;

        private Entry(GTN_WirelessMEIOHub hub, long lastSeenNanos) {
            this.hub = new WeakReference<>(hub);
            this.lastSeenNanos = lastSeenNanos;
        }

        private GTN_WirelessMEIOHub getHub() {
            return hub.get();
        }
    }
}
