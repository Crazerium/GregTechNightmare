package com.EvgenWarGold.GregTechNightmare.Api;

import gregtech.api.enums.Mods;

public abstract class ModHandler {

    public final String mod;

    public ModHandler(String modID) {
        this.mod = modID;
    }

    public ModHandler(Mods mod) {
        this(mod.ID);
    }
}
