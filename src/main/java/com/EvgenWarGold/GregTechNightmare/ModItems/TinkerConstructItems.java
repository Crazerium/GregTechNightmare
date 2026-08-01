package com.EvgenWarGold.GregTechNightmare.ModItems;

import gregtech.api.enums.Mods;
import net.minecraft.item.ItemStack;

public enum TinkerConstructItems {

    // spotless:off
    MiniatureRedHeart("heartCanister", "Miniature Red Heart", 1),
    MiniatureYellowHeart("heartCanister", "Miniature Yellow Heart", 3),
    MiniatureGreenHeart("heartCanister", "Miniature Green Heart", 5),
    ;
    //spotless:on

    private final String unlocalizedName;
    private final String localizedName;
    private final int meta;
    private ModItem cachedItem;

    TinkerConstructItems(String unlocalizedName, String localizedName, int meta) {
        this.unlocalizedName = unlocalizedName;
        this.localizedName = localizedName;
        this.meta = meta;
    }

    TinkerConstructItems(String unlocalizedName, String localizedName) {
        this(unlocalizedName, localizedName, 0);
    }

    private ModItem getItem() {
        if (cachedItem == null) {
            cachedItem = new ModItem(Mods.TinkerConstruct, unlocalizedName, localizedName, meta);
        }
        return cachedItem;
    }

    public ItemStack get() {
        return getItem().get();
    }

    public ItemStack get(int count) {
        return getItem().get(count);
    }
}
