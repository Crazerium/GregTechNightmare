package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public class NewHorizonsCoreModItems extends ModHandler {

    public final ModItem HighEnergyFlowCircuit;

    public NewHorizonsCoreModItems() {
        super(Mods.NewHorizonsCoreMod);

        HighEnergyFlowCircuit = createItem("item.HighEnergyFlowCircuit", "High Energy Flow Circuit");
    }
}
