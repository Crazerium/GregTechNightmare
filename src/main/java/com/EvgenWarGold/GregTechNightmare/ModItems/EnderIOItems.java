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

        ZombieElectrode = new ModItem(mod, "itemFrankenSkull", 0, "Zombie Electrode");
        ZLogicCapacitor = new ModItem(mod, "itemFrankenSkull", 1, "Z-Logic Capacitor");
        TormentedEndermanHead = new ModItem(mod, "blockEndermanSkull", 2, "Tormented Enderman Head");
        EnderResonator = new ModItem(mod, "itemFrankenSkull", 3, "Ender Resonator");
        SkeletalContractor = new ModItem(mod, "itemFrankenSkull", 5, "Skeletal Contractor");
        GuardianDiode = new ModItem(mod, "itemFrankenSkull", 6, "Guardian Diode");
        BasicCapacitor = new ModItem(mod, "itemBasicCapacitor", 0, "Basic Capacitor");
        DoubleLayerCapacitor = new ModItem(mod, "itemBasicCapacitor", 1, "Double-Layer Capacitor");
        OctadicCapacitor = new ModItem(mod, "itemBasicCapacitor", 2, "Octadic Capacitor");
        CrystallineCapacitor = new ModItem(mod, "itemBasicCapacitor", 3, "Crystalline Capacitor");
        MelodicCapacitor = new ModItem(mod, "itemBasicCapacitor", 4, "Melodic Capacitor");
        StellarCapacitor = new ModItem(mod, "itemBasicCapacitor", 5, "Stellar Capacitor");
        TotemicCapacitor = new ModItem(mod, "itemBasicCapacitor", 6, "Totemic Capacitor");
        SilverCapacitor = new ModItem(mod, "itemBasicCapacitor", 7, "Silver Capacitor");
        EndergeticCapacitor = new ModItem(mod, "itemBasicCapacitor", 8, "Endergetic Capacitor");
        EndergisedCapacitor = new ModItem(mod, "itemBasicCapacitor", 9, "Endergised Capacitor");
        EndermanHead = new ModItem(mod, "blockEndermanSkull", 0, "Enderman Head");
        PulsatingCrystal = new ModItem(mod, "itemMaterial", 5, "Pulsating Crystal");
    }
}
