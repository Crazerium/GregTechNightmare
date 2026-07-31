package com.EvgenWarGold.GregTechNightmare.ModItems;

import gregtech.api.enums.Mods;
import net.minecraft.item.ItemStack;

public enum NewHorizonsCoreModItems {

    // spotless:off
    HighEnergyFlowCircuit("HighEnergyFlowCircuit", "High Energy Flow Circuit"),
        ;
    //spotless:on

    private final String unlocalizedName;
    private final String localizedName;
    private final int meta;
    private ModItem cachedItem;

    NewHorizonsCoreModItems(String unlocalizedName, String localizedName, int meta) {
        this.unlocalizedName = unlocalizedName;
        this.localizedName = localizedName;
        this.meta = meta;
    }

    NewHorizonsCoreModItems(String unlocalizedName, String localizedName) {
        this(unlocalizedName, localizedName, 0);
    }

    private ModItem getItem() {
        if (cachedItem == null) {
            cachedItem = new ModItem(Mods.NewHorizonsCoreMod, unlocalizedName, localizedName, meta);
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
