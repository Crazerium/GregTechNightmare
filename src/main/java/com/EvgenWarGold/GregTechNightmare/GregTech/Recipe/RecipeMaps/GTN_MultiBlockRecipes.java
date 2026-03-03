package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_RecipeUtils.getCircuit;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_RecipeUtils.getCircuits;
import static gregtech.api.enums.TierEU.RECIPE_EV;
import static gregtech.api.enums.TierEU.RECIPE_LV;
import static gregtech.api.enums.TierEU.RECIPE_LuV;
import static gregtech.api.enums.TierEU.RECIPE_MV;
import static gregtech.api.util.GTModHandler.addCraftingRecipe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.ModBlocks;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict;

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

        // Medium Power Circuit Assembler
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Casing_MV.get(10),
                ItemList.Machine_MV_CircuitAssembler.get(1),
                ItemList.Electric_Pump_MV.get(2),
                ItemList.Robot_Arm_MV.get(2),
                getCircuits(Materials.MV, 2)
            )
            .itemOutputs(GTN_ItemList.MediumPowerCircuitAssembler.get(1))

            .eut(RECIPE_MV)
            .duration(20 * 60)
            .addTo(RecipeMaps.assemblerRecipes);

        // Advanced Coke Oven
        addCraftingRecipe(
            GTN_ItemList.AdvancedCokeOven.get(1),
            new Object[]{"AAA", "A A", "AAA",
                'A', GTN_Casings.CokeOvenCasing.getItemStack()
            });

        // Node Energizer
        addCraftingRecipe(
            GTN_ItemList.NodeEnergizer.get(1),
            new Object[]{"ABA", "BCB", "ABA",
                'A', GTN_Casings.MagicCasing.getItemStack(),
                'B', ItemList.Field_Generator_HV.get(1),
                'C', OrePrefixes.frameGt.get(Materials.Thaumium)
            });

        // Large Arcane Assembler
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTN_Casings.MagicCasing.getItemStack(32),
                ModBlocks.THAUMIC_ENERGETICS_BLOCKS.ARCANE_ASSEMBLER.getItemStack(1),
                ItemList.Electric_Piston_LuV.get(8),
                ItemList.Electric_Motor_LuV.get(16),
                GTN_OreDict.getBlock(Materials.Ichorium, 8),
                getCircuits(Materials.ZPM, 4)
            )
            .itemOutputs(GTN_ItemList.LargeArcaneAssembler.get(1))

            .eut(RECIPE_LuV)
            .duration(150 * 60)
            .addTo(RecipeMaps.assemblerRecipes);

        // Tree Sprouter
        GTValues.RA.stdBuilder()
            .itemInputs(
                getCircuits(Materials.LV, 4),
                ItemList.Robot_Arm_LV.get(1),
                ItemList.Conveyor_Module_LV.get(2),
                GTN_Casings.AborealCasing.getItemStack(4),
                ItemList.Hull_LV.get(1)
            )
            .fluidInputs(
                Materials.Water.getFluid(16_000)
            )
            .itemOutputs(GTN_ItemList.TreeSprouter.get(1))

            .eut(RECIPE_LV)
            .duration(20 * 60)
            .addTo(RecipeMaps.assemblerRecipes);

        // Creosote Engine
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTN_Casings.SolidSteelMachineCasing.getItemStack(8),
                GTN_Casings.SteelGearBoxCasing.getItemStack(4),
                ItemList.Electric_Pump_LV.get(2),
                ItemList.Hull_LV.get(1)
            )
            .fluidInputs(
                Materials.Creosote.getFluid(4_000)
            )
            .itemOutputs(GTN_ItemList.CreosoteEngine.get(1))

            .eut(RECIPE_LV)
            .duration(20 * 60)
            .addTo(RecipeMaps.assemblerRecipes);

        // Medium Power Wiremill
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Casing_MV.get(10),
                ItemList.Machine_MV_Wiremill.get(1),
                ItemList.Electric_Piston_MV.get(2),
                ItemList.Robot_Arm_MV.get(2),
                getCircuits(Materials.MV, 2)
            )
            .itemOutputs(GTN_ItemList.MediumPowerWireMill.get(1))

            .eut(RECIPE_MV)
            .duration(20 * 60)
            .addTo(RecipeMaps.assemblerRecipes);

        // Medium Power Engraver
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Casing_MV.get(10),
                ItemList.Machine_MV_LaserEngraver.get(1),
                ItemList.Electric_Pump_MV.get(2),
                ItemList.Electric_Piston_MV.get(2),
                ItemList.Robot_Arm_MV.get(2),
                getCircuits(Materials.HV, 2)
            )
            .itemOutputs(GTN_ItemList.MediumPowerEngraver.get(1))

            .eut(RECIPE_MV)
            .duration(20 * 60)
            .addTo(RecipeMaps.assemblerRecipes);

        // Extreme Power Circuit Assembler
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTN_Casings.StableTitaniumMachineCasing.getItemStack(8),
                ItemList.Emitter_EV.get(2),
                ItemList.Sensor_EV.get(2),
                ItemList.Field_Generator_EV.get(2),
                ItemList.Machine_EV_CircuitAssembler.get(1)
            )
            .fluidInputs(
                Materials.Radon.getGas(8_000)
            )
            .itemOutputs(GTN_ItemList.ExtremePowerCircuitAssembler.get(1))

            .eut(RECIPE_EV)
            .duration(20 * 60)
            .addTo(RecipeMaps.assemblerRecipes);

        //spotless:on
    }
}
