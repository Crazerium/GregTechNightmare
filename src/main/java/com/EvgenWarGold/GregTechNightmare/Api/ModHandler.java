package com.EvgenWarGold.GregTechNightmare.Api;

import org.jetbrains.annotations.NotNull;

import gregtech.api.enums.Mods;

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

    protected ModItem createItem(@NotNull String unlocalizedName, @NotNull String localizedName, int meta) {
        return new ModItem(mod, unlocalizedName, meta, localizedName);
    }

    protected ModItem createItem(@NotNull String unlocalizedName, @NotNull String localizedName) {
        return new ModItem(mod, unlocalizedName, 0, unlocalizedName);
    }
}
