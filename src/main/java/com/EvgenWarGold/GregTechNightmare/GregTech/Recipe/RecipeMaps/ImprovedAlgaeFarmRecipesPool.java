package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;

import gregtech.api.enums.GTValues;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;

public class ImprovedAlgaeFarmRecipesPool {

    private static ItemStack algae(int meta, int amount) {
        return GTModHandler.getModItem("miscutils", "item.BasicAgrichemItem", amount, meta);
    }

    public static void init() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .itemOutputs(algae(0, 1), algae(1, 1), algae(2, 1), algae(3, 1), algae(4, 1))
            .duration(200)
            .eut(0)
            .addTo(GTN_Recipe.ImprovedAlgaeFarmRecipes);

        for (int i = 0; i < 5; i++) {
            GTValues.RA.stdBuilder()
                .itemInputs(GTUtility.getIntegratedCircuit(i + 2))
                .itemOutputs(algae(i, 5))
                .duration(200)
                .eut(0)
                .addTo(GTN_Recipe.ImprovedAlgaeFarmRecipes);
        }
    }
}
