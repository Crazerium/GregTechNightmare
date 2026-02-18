package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_RecipeUtils.getCircuit;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_RecipeUtils.getCircuits;
import static gregtech.api.enums.TierEU.RECIPE_MV;
import static gregtech.api.util.GTModHandler.addCraftingRecipe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;

public class GTN_MultiBlockRecipes {

    public static void init() {
        // spotless:off

        // Advanced BBF
        addCraftingRecipe(
            GTN_ItemList.AdvancedBBF.get(1),
            new Object[]{"ABA", "BCB", "BBB",
                'A', OrePrefixes.frameGt.get(Materials.Steel),
                'B', ItemList.Casing_Firebricks,
                'C', ItemList.Machine_Bricked_BlastFurnace
            });

        // Bronze Void Miner
        addCraftingRecipe(
            GTN_ItemList.BronzeVoidMiner.get(1),
            new Object[]{"ABA", "BCB", "EDE",
                'A', OrePrefixes.frameGt.get(Materials.Bronze),
                'B', ItemList.Casing_BronzePlatedBricks,
                'D', ItemList.Machine_Bronze_Boiler.get(1),
                'C', new ItemStack(Items.diamond_pickaxe),
                'E', ItemList.Casing_Firebox_Bronze
            });

        // Low Power Void Miner
        addCraftingRecipe(
            GTN_ItemList.LowPowerVoidMiner.get(1),
            new Object[]{"AAA", "BCB", "DDD",
                'A', GTN_Casings.BoltedCobaltCasing.getItemStack(),
                'B', getCircuit(Materials.LV),
                'C', GTN_ItemList.BronzeVoidMiner.get(1),
                'D', GTN_Casings.SolidSteelMachineCasing.getItemStack()
            });

        // Medium Power Bender
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Casing_MV.get(10),
                ItemList.Machine_MV_Bender.get(1),
                ItemList.Electric_Piston_MV.get(2),
                ItemList.Electric_Motor_MV.get(2),
                getCircuits(Materials.MV, 2)
            )
            .itemOutputs(GTN_ItemList.MediumPowerBender.get(1))

            .eut(RECIPE_MV)
            .duration(20 * 60)
            .addTo(RecipeMaps.assemblerRecipes);

        // Medium Power Extruder
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Casing_MV.get(10),
                ItemList.Machine_MV_Extruder.get(1),
                ItemList.Electric_Piston_MV.get(2),
                ItemList.Robot_Arm_MV.get(2),
                getCircuits(Materials.MV, 2)
            )
            .itemOutputs(GTN_ItemList.MediumPowerExtruder.get(1))

            .eut(RECIPE_MV)
            .duration(20 * 60)
            .addTo(RecipeMaps.assemblerRecipes);

        // Medium Power Assembler
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Casing_MV.get(10),
                ItemList.Machine_MV_Assembler.get(1),
                ItemList.Electric_Pump_MV.get(2),
                ItemList.Robot_Arm_MV.get(2),
                getCircuits(Materials.MV, 2)
            )
            .itemOutputs(GTN_ItemList.MediumPowerAssembler.get(1))

            .eut(RECIPE_MV)
            .duration(20 * 60)
            .addTo(RecipeMaps.assemblerRecipes);
        //spotless:on
    }
}
