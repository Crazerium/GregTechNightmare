package com.EvgenWarGold.GregTechNightmare.ModItems;

import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public enum EnderIOItems {

    // spotless:off
    ZombieElectrode("itemFrankenSkull", "Zombie Electrode"),
    ZLogicCapacitor("itemFrankenSkull", "Z-Logic Capacitor", 1),
    TormentedEndermanHead("blockEndermanSkull", "Tormented Enderman Head", 2),
    EnderResonator("itemFrankenSkull", "Ender Resonator", 3),
    SkeletalContractor("itemFrankenSkull", "Skeletal Contractor", 5),
    GuardianDiode("itemFrankenSkull", "Guardian Diode", 6),
    BasicCapacitor("itemBasicCapacitor", "Basic Capacitor"),
    DoubleLayerCapacitor("itemBasicCapacitor", "Double-Layer Capacitor", 1),
    OctadicCapacitor("itemBasicCapacitor", "Octadic Capacitor", 2),
    CrystallineCapacitor("itemBasicCapacitor", "Crystalline Capacitor", 3),
    MelodicCapacitor("itemBasicCapacitor", "Melodic Capacitor", 4),
    StellarCapacitor("itemBasicCapacitor", "Stellar Capacitor", 5),
    TotemicCapacitor("itemBasicCapacitor", "Totemic Capacitor", 6),
    SilverCapacitor("itemBasicCapacitor", "Silver Capacitor", 7),
    EndergeticCapacitor("itemBasicCapacitor", "Endergetic Capacitor", 8),
    EndergisedCapacitor("itemBasicCapacitor", "Endergised Capacitor", 9),
    EndermanHead("blockEndermanSkull", "Enderman Head"),
    PulsatingCrystal("itemMaterial", "Pulsating Crystal", 5);
    ;
    //spotless:on

    private final String unlocalizedName;
    private final String localizedName;
    private final int meta;
    private ModItem cachedItem;

    EnderIOItems(String unlocalizedName, String localizedName, int meta) {
        this.unlocalizedName = unlocalizedName;
        this.localizedName = localizedName;
        this.meta = meta;
    }

    EnderIOItems(String unlocalizedName, String localizedName) {
        this(unlocalizedName, localizedName, 0);
    }

    private ModItem getItem() {
        if (cachedItem == null) {
            cachedItem = new ModItem(Mods.EnderIO, unlocalizedName, localizedName, meta);
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
