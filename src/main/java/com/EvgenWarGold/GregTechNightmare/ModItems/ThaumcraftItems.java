package com.EvgenWarGold.GregTechNightmare.ModItems;

import gregtech.api.enums.Mods;
import net.minecraft.item.ItemStack;

public enum ThaumcraftItems {

    // spotless:off
    PrimordialPearl("ItemEldritchObject", "Primordial Pearl", 3)
    ;
    //spotless:on

    private final String unlocalizedName;
    private final String localizedName;
    private final int meta;
    private ModItem cachedItem;

    ThaumcraftItems(String unlocalizedName, String localizedName, int meta) {
        this.unlocalizedName = unlocalizedName;
        this.localizedName = localizedName;
        this.meta = meta;
    }

    ThaumcraftItems(String unlocalizedName, String localizedName) {
        this(unlocalizedName, localizedName, 0);
    }

    private ModItem getItem() {
        if (cachedItem == null) {
            cachedItem = new ModItem(Mods.Thaumcraft, unlocalizedName, localizedName, meta);
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
