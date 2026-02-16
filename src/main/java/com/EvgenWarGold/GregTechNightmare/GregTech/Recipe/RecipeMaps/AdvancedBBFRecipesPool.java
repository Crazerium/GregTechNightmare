package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.util.GTOreDictUnificator;

public class AdvancedBBFRecipesPool {

    public static void init() {
        final IRecipeMap AdvancedBBF = GTN_Recipe.AdvancedBBFRecipes;

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.iron_ingot, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.WroughtIron, 1L))
            .eut(0)
            .duration(10 * 20)
            .addTo(AdvancedBBF);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.WroughtIron, 1L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 1L))
            .eut(0)
            .duration(10 * 20)
            .addTo(AdvancedBBF);
    }
}
