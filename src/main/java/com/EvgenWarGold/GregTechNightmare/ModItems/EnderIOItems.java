package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public class EnderIOItems extends ModHandler {

    public final ModItem ZombieElectrode;
    public final ModItem ZLogicCapacitor;
    public final ModItem TormentedEndermanHead;
    public final ModItem EnderResonator;
    public final ModItem SkeletalContractor;
    public final ModItem GuardianDiode;
    public final ModItem BasicCapacitor;
    public final ModItem DoubleLayerCapacitor;
    public final ModItem OctadicCapacitor;
    public final ModItem CrystallineCapacitor;
    public final ModItem MelodicCapacitor;
    public final ModItem StellarCapacitor;
    public final ModItem TotemicCapacitor;
    public final ModItem SilverCapacitor;
    public final ModItem EndergeticCapacitor;
    public final ModItem EndergisedCapacitor;
    public final ModItem EndermanHead;
    public final ModItem PulsatingCrystal;

    public EnderIOItems() {
        super(Mods.EnderIO);

        ZombieElectrode = createItem("itemFrankenSkull", "Zombie Electrode");
        ZLogicCapacitor = createItem("itemFrankenSkull", "Z-Logic Capacitor", 1);
        TormentedEndermanHead = createItem("blockEndermanSkull", "Tormented Enderman Head", 2);
        EnderResonator = createItem("itemFrankenSkull", "Ender Resonator", 3);
        SkeletalContractor = createItem("itemFrankenSkull", "Skeletal Contractor", 5);
        GuardianDiode = createItem("itemFrankenSkull", "Guardian Diode", 6);
        BasicCapacitor = createItem("itemBasicCapacitor", "Basic Capacitor");
        DoubleLayerCapacitor = createItem("itemBasicCapacitor", "Double-Layer Capacitor", 1);
        OctadicCapacitor = createItem("itemBasicCapacitor", "Octadic Capacitor", 2);
        CrystallineCapacitor = createItem("itemBasicCapacitor", "Crystalline Capacitor", 3);
        MelodicCapacitor = createItem("itemBasicCapacitor", "Melodic Capacitor", 4);
        StellarCapacitor = createItem("itemBasicCapacitor", "Stellar Capacitor", 5);
        TotemicCapacitor = createItem("itemBasicCapacitor", "Totemic Capacitor", 6);
        SilverCapacitor = createItem("itemBasicCapacitor", "Silver Capacitor", 7);
        EndergeticCapacitor = createItem("itemBasicCapacitor", "Endergetic Capacitor", 8);
        EndergisedCapacitor = createItem("itemBasicCapacitor", "Endergised Capacitor", 9);
        EndermanHead = createItem("blockEndermanSkull", "Enderman Head");
        PulsatingCrystal = createItem("itemMaterial", "Pulsating Crystal", 5);
    }
}
