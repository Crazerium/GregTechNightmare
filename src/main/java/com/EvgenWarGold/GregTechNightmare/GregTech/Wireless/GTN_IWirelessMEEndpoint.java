package com.EvgenWarGold.GregTechNightmare.GregTech.Wireless;

import appeng.me.helpers.AENetworkProxy;

public interface GTN_IWirelessMEEndpoint {

    GTN_WirelessMELink gtn$getWirelessLink();

    AENetworkProxy gtn$getWirelessProxy();

    default boolean gtn$isWirelessLinked() {
        GTN_WirelessMELink link = gtn$getWirelessLink();
        return link != null && link.isLinked();
    }
}
