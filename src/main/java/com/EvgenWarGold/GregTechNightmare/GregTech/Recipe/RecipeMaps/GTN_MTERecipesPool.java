package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict.getFluid;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict.getPlate;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict.getWireGt01;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_RecipeUtils.getCircuits;
import static gregtech.api.enums.TierEU.RECIPE_LuV;
import static gregtech.api.util.GTModHandler.addCraftingRecipe;
import static gregtech.api.util.GTRecipeBuilder.MINUTES;
import static gregtech.api.util.GTRecipeConstants.RESEARCH_ITEM;
import static gregtech.api.util.GTRecipeConstants.SCANNING;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.BotaniaBlocks;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.ThaumcraftBlocks;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.recipe.Scanning;

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

        // Mana Hatch
        addCraftingRecipe(
            GTN_ItemList.ManaHatch.get(1),
            new Object[]{
                "ACA",
                "DBG",
                "AEA",
                'A', BotaniaBlocks.LivingRock.getItemStack(),
                'B', BotaniaBlocks.ManaPool.getItemStack(),
                'D', ItemList.Robot_Arm_MV.get(1),
                'C', getPlate(Materials.Thaumium),
                'E', ThaumcraftBlocks.AlchemicalConstruct.getItemStack(),
                'G', ItemList.Conveyor_Module_MV.get(1)
            });

        // ME Wildcard Pattern Buffer
        GTValues.RA.stdBuilder()
            .metadata(RESEARCH_ITEM, ItemList.Hatch_CraftingInput_Bus_ME_ItemOnly.get(1))
            .metadata(SCANNING, new Scanning(30 * MINUTES, 30))
            .itemInputs(
                ItemList.Hatch_CraftingInput_Bus_ME_ItemOnly.get(16),
                ItemList.Robot_Arm_LuV.get(4),
                ItemList.Conveyor_Module_LuV.get(4),
                getCircuits(Materials.LuV, 8),
                getWireGt01(Materials.SuperconductorLuV, 64)
            )
            .fluidInputs(
                getFluid(Materials.Lubricant, 512_000),
                getFluid(Materials.SuperCoolant, 256_000)
            )
            .itemOutputs(GTN_ItemList.WildcardPatternBuffer.get(1))

            .eut(RECIPE_LuV)
            .duration(20 * 60)
            .addTo(GTRecipeConstants.AssemblyLine);
        // spotless:on
    }
}
