package com.EvgenWarGold.GregTechNightmare.Api;

import gregtech.api.enums.Mods;
import org.jetbrains.annotations.NotNull;

public abstract class ModHandler {

    public final String mod;

    public ModHandler(String modID) {
        this.mod = modID;
    }

    public ModHandler(Mods mod) {
        this(mod.ID);
    }

    protected ModBlock createBlock(@NotNull String unlocalizedName, int meta, @NotNull String localizedName) {
        return new ModBlock(mod, unlocalizedName, meta, localizedName);
    }

    protected ModBlock createBlock(@NotNull String unlocalizedName, int meta) {
        return new ModBlock(mod, unlocalizedName, meta, unlocalizedName);
    }

    protected ModBlock createBlock(@NotNull String unlocalizedName) {
        return new ModBlock(mod, unlocalizedName, 0, unlocalizedName);
    }
}
