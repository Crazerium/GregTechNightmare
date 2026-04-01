package com.EvgenWarGold.GregTechNightmare.Utils;

import java.util.Map;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.recipe.metadata.IRecipeMetadataStorage;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;

public class GTN_RecipeUtils {

    public static Object getCircuit(Materials circuitTier) {
        return OrePrefixes.circuit.get(circuitTier);
    }

    public static Object[] getCircuits(Materials circuitTier, int amount) {
        return new Object[] { OrePrefixes.circuit.get(circuitTier), amount };
    }

    public static void cloneRecipeMap(RecipeMap<?> source, RecipeMap<?> target) {
        for (GTRecipe recipe : source.getBackend()
            .getAllRecipes()) {
            GTRecipeBuilder builder = GTValues.RA.stdBuilder();

            builder.itemInputs(recipe.mInputs)
                .itemOutputs(recipe.mOutputs)
                .fluidInputs(recipe.mFluidInputs)
                .fluidOutputs(recipe.mFluidOutputs)
                .duration(recipe.mDuration)
                .eut(recipe.mEUt)
                .specialValue(recipe.mSpecialValue)
                .outputChances(recipe.mChances);

            IRecipeMetadataStorage metadata = recipe.getMetadataStorage();
            if (metadata != null) {
                for (Map.Entry entry : metadata.getEntries()) {
                    builder.metadata((RecipeMetadataKey) entry.getKey(), entry.getValue());
                }
            }

            String[] neiDesc = recipe.getNeiDesc();
            if (neiDesc != null) {
                builder.setNEIDesc(neiDesc);
            }

            builder.addTo(target);
        }
    }
}
