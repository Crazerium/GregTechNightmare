package com.EvgenWarGold.GregTechNightmare.ModItems;

import net.minecraft.item.ItemStack;

import gregtech.api.enums.Mods;

public enum AppliedEnergisticsItems {

    // spotless:off
    MEController("tile.BlockController", "ME Controller"),
    AccelerationCard("item.ItemMultiMaterial", "Acceleration Card", 30),
    CapacityCard("item.ItemMultiMaterial", "Capacity Card", 27),
    ;
    //spotless:on

    private final String unlocalizedName;
    private final String localizedName;
    private final int meta;
    private ModItem cachedItem;

    AppliedEnergisticsItems(String unlocalizedName, String localizedName, int meta) {
        this.unlocalizedName = unlocalizedName;
        this.localizedName = localizedName;
        this.meta = meta;
    }

    AppliedEnergisticsItems(String unlocalizedName, String localizedName) {
        this(unlocalizedName, localizedName, 0);
    }

    private ModItem getItem() {
        if (cachedItem == null) {
            cachedItem = new ModItem(Mods.AppliedEnergistics2, unlocalizedName, localizedName, meta);
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
