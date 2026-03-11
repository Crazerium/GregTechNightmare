package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;

public class GasCollectorRecipesPool {
    public static void init() {
        GTValues.RA.stdBuilder()
            .fluidOutputs(Materials.Air.getGas(1000))
            .duration(20)
            .eut(TierEU.RECIPE_LV)
            .addTo(GTN_Recipe.GasCollectorRecipes);
    }
}
