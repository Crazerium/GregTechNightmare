package com.EvgenWarGold.GregTechNightmare.ModItems;

import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public enum AppliedEnergisticsItems {

    // spotless:off
    MEController("tile.BlockController", "ME Controller"),
    AccelerationCard("item.ItemMultiMaterial", "Acceleration Card", 30),
    CapacityCard("item.ItemMultiMaterial", "Capacity Card", 27),
    MEInterface("tile.BlockInterface", "ME Interface"),
    FluixCrystal("item.ItemMultiMaterial", "Fluix Crystal", 7),
    EngineeringProcessor("item.ItemMultiMaterial", "Engineering Processor", 24),
    Cell1kPart("item.ItemMultiMaterial", "1k ME Storage Component", 35),
    Cell4kPart("item.ItemMultiMaterial", "4k ME Storage Component", 36),
    Cell16kPart("item.ItemMultiMaterial", "16k ME Storage Component", 37),
    Cell64kPart("item.ItemMultiMaterial", "64k ME Storage Component", 38),
    Cell256kPart("item.ItemMultiMaterial", "256k ME Storage Component", 57),
    EmptyStorageCell("item.ItemMultiMaterial", "ME Storage Housing", 39),
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
