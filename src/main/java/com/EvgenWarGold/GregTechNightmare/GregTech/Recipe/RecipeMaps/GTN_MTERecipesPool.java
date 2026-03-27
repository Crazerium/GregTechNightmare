package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static gregtech.api.util.GTModHandler.addCraftingRecipe;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;

public class GTN_MTERecipesPool {

    public static void init() {
        // spotless:off
        // Sensor Hatch
        addCraftingRecipe(
            GTN_ItemList.SensorHatch.get(1),
            new Object[]{
                "ACA",
                "GBG",
                "AEA",
                'A', GTN_OreDict.getDense(Materials.Lead),
                'B', ItemList.Hull_HV.get(1),
                'C', ItemList.Sensor_HV.get(1),
                'E', ItemList.Emitter_HV.get(1),
                'G', GTN_OreDict.getWireGt16(Materials.Lead)
            });
        // spotless:on
    }
}
