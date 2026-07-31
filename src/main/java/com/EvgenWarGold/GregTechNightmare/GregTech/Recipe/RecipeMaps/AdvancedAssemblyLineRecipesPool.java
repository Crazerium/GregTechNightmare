package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static gregtech.api.util.GTRecipeBuilder.MINUTES;
import static gregtech.api.util.GTRecipeConstants.RESEARCH_ITEM;
import static gregtech.api.util.GTRecipeConstants.SCANNING;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_RecipeBuilder;
import com.EvgenWarGold.GregTechNightmare.ModItems.AE2FluidCraftItems;
import com.EvgenWarGold.GregTechNightmare.ModItems.AppliedEnergisticsItems;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.recipe.Scanning;
import gtPlusPlus.core.material.MaterialsAlloy;

public class AdvancedAssemblyLineRecipesPool {

    public static void init() {
        GTN_RecipeBuilder.builder()
            .metadata(RESEARCH_ITEM, ItemList.Hatch_Input_Bus_ME_Advanced.get(1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, 30))
            .itemInputs(
                ItemList.Hatch_Input_Bus_ME_Advanced.get(1),
                ItemList.Conveyor_Module_IV.get(1),
                ItemList.Electric_Pump_IV.get(1),
                AppliedEnergisticsItems.MEController.get(1),
                AppliedEnergisticsItems.AccelerationCard.get(8),
                AppliedEnergisticsItems.CapacityCard.get(8))
            .fluidInputs(MaterialsAlloy.INDALLOY_140.getFluidStack(8_000), Materials.Lubricant.getFluid(8_000))
            .itemOutputs(AE2FluidCraftItems.SuperStockReplenisher.get(1))
            .recipeLUV()
            .durationInMinutes(1)
            .addTo(GTRecipeConstants.AssemblyLine);
    }
}
