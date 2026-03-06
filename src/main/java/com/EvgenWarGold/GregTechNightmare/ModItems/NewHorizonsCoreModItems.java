package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public class NewHorizonsCoreModItems extends ModHandler {

    public final ModItem HighEnergyFlowCircuit;

    public NewHorizonsCoreModItems() {
        super(Mods.NewHorizonsCoreMod);
        HighEnergyFlowCircuit = new ModItem(mod, "item.HighEnergyFlowCircuit", 0, "High Energy Flow Circuit");

    }
}
