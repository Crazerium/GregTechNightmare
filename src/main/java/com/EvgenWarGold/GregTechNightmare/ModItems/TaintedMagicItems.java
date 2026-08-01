package com.EvgenWarGold.GregTechNightmare.ModItems;

import net.minecraft.item.ItemStack;

import gregtech.api.enums.Mods;

public enum TaintedMagicItems {

    // spotless:off
    WandFocusTime("ItemFocusTime", "Wand Focus: Time"),
    ;
    //spotless:on

    private final String unlocalizedName;
    private final String localizedName;
    private final int meta;
    private ModItem cachedItem;

    TaintedMagicItems(String unlocalizedName, String localizedName, int meta) {
        this.unlocalizedName = unlocalizedName;
        this.localizedName = localizedName;
        this.meta = meta;
    }

    TaintedMagicItems(String unlocalizedName, String localizedName) {
        this(unlocalizedName, localizedName, 0);
    }

    private ModItem getItem() {
        if (cachedItem == null) {
            cachedItem = new ModItem(Mods.TaintedMagic, unlocalizedName, localizedName, meta);
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
