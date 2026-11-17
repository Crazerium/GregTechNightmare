package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.createItem;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.setStackSize;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict.getFrameGt;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict.getPlate;
import static gregtech.api.enums.TierEU.RECIPE_EV;
import static gregtech.api.enums.TierEU.RECIPE_HV;
import static gregtech.api.enums.TierEU.RECIPE_LV;
import static gregtech.api.enums.TierEU.RECIPE_LuV;
import static gregtech.api.enums.TierEU.RECIPE_MV;
import static gregtech.api.util.GTModHandler.addShapelessCraftingRecipe;

import net.minecraft.init.Blocks;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.GTN_Casings;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.ThaumcraftBlocks;
import com.EvgenWarGold.GregTechNightmare.ModItems.AppliedEnergisticsItems;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialsAlloy;

public class GTN_META_BLOCK_CASING_01_RecipesPool {

    public static void init() {
        // spotless:off

        // Coke Oven Casing
        addShapelessCraftingRecipe(
            GTN_ItemList.CokeOvenCasing.get(1),
            new Object[]{ GTN_ItemList.AdvancedClay.get(1),
                GTN_ItemList.AdvancedClay.get(1),
                GTN_ItemList.AdvancedClay.get(1),
                GTN_ItemList.AdvancedClay.get(1)});

        // Arboreal Casing
        GTValues.RA.stdBuilder()
            .itemInputs(
                MaterialsAlloy.TUMBAGA.getFrameBox(1),
                MaterialsAlloy.TUMBAGA.getPlate(4),
                MaterialsAlloy.TUMBAGA.getRing(1),
                MaterialsAlloy.TUMBAGA.getScrew(2),
                setStackSize(createItem(Blocks.log, 0), 4)
            )
            .fluidInputs(
                Materials.Steam.getGas(16_000)
            )
            .itemOutputs(GTN_ItemList.ArborealCasing.get(1))

            .eut(RECIPE_LV)
            .duration(20 * 5)
            .addTo(RecipeMaps.assemblerRecipes);

        // Soul Casing
        GTValues.RA.stdBuilder()
            .itemInputs(
                Materials.Soularium.getPlates(2),
                Materials.Soularium.getIngots(4),
                Materials.Steel.getBlocks(1)
            )
            .itemOutputs(GTN_ItemList.SoulCasing.get(1))

            .eut(RECIPE_MV)
            .duration(20 * 10)
            .addTo(RecipeMaps.assemblerRecipes);

        // Runic Matrix Casing
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTN_Casings.MagicCasing.getItemStack(1),
                getPlate(Materials.Thaumium, 6),
                GTUtility.getIntegratedCircuit(1)
            )
            .itemOutputs(GTN_ItemList.RunicMatrixCasing.get(1))

            .eut(RECIPE_EV)
            .duration(20 * 10)
            .addTo(RecipeMaps.assemblerRecipes);

        // Alchemy Construct Casing
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTN_Casings.MagicCasing.getItemStack(4),
                ThaumcraftBlocks.AlchemicalConstruct.getItemStack(1),
                getPlate(Materials.Thaumium, 8),
                GTUtility.getIntegratedCircuit(2)
            )
            .itemOutputs(GTN_ItemList.AlchemyConstructCasing.get(4))

            .eut(RECIPE_HV)
            .duration(20 * 10)
            .addTo(RecipeMaps.assemblerRecipes);

        // Arcane Assembler Casing
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTN_Casings.MagicCasing.getItemStack(4),
                getPlate(Materials.Void, 4),
                getPlate(Materials.Thaumium, 8),
                GTUtility.getIntegratedCircuit(3)
            )
            .itemOutputs(GTN_ItemList.ArcaneAssemblerCasing.get(4))

            .eut(RECIPE_EV)
            .duration(20 * 10)
            .addTo(RecipeMaps.assemblerRecipes);

        // Data Drive Machine Casing
        GTValues.RA.stdBuilder()
            .itemInputs(
                getFrameGt(Materials.Titanium, 1),
                getPlate(Materials.Titanium, 6),
                AppliedEnergisticsItems.EngineeringProcessor.get(2),
                AppliedEnergisticsItems.FluixCrystal.get(4)
            )
            .fluidInputs(Materials.SolderingAlloy.getMolten(144))
            .itemOutputs(GTN_Casings.DataDriveMachineCasing.getItemStack(2))
            .eut(RECIPE_LuV)
            .duration(20 * 20)
            .addTo(RecipeMaps.assemblerRecipes);

        // ME Storage Cores
        GTN_ItemList[] cores = { GTN_ItemList.MEStorageCore1M, GTN_ItemList.MEStorageCore64M,
            GTN_ItemList.MEStorageCore4G, GTN_ItemList.MEStorageCore256G };
        ItemList[] emitters = { ItemList.Emitter_LuV, ItemList.Emitter_ZPM, ItemList.Emitter_UV, ItemList.Emitter_UHV };
        for (int tier = 0; tier < cores.length; tier++) {
            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTN_Casings.DataDriveMachineCasing.getItemStack(1),
                    tier == 0 ? AppliedEnergisticsItems.Cell256kPart.get(4) : cores[tier - 1].get(64),
                    AppliedEnergisticsItems.EngineeringProcessor.get(8),
                    emitters[tier].get(2)
                )
                .itemOutputs(cores[tier].get(1))
                .eut(30720L << (tier * 2)).duration(20 * 60)
                .addTo(RecipeMaps.assemblerRecipes);
        }

        //spotless:on
    }
}
