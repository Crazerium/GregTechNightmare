package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict.getFluid;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict.getPlate;
import static gregtech.api.enums.Materials.Water;

import com.EvgenWarGold.GregTechNightmare.ModItems.EnderIOItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;

public class ImprovedSliceNSpliceRecipesPool {

    public static void init() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                getPlate(Materials.EnergeticAlloy, 2),
                getPlate(Materials.Silicon, 2),
                EnderIOItems.BasicCapacitor.get(1),
                new ItemStack(Items.skull, 1, 2))
            .itemOutputs(EnderIOItems.ZombieElectrode.get(1))
            .eut(500)
            .duration(1000)
            .addTo(GTN_Recipe.ImprovedSliceNSpliceRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                getPlate(Materials.Soularium, 2),
                getPlate(Materials.Silicon, 2),
                getPlate(Materials.RedAlloy, 1),
                new ItemStack(Items.skull, 1, 2))
            .itemOutputs(EnderIOItems.ZLogicCapacitor.get(1))
            .eut(500)
            .duration(2000)
            .addTo(GTN_Recipe.ImprovedSliceNSpliceRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                getPlate(Materials.Soularium, 2),
                EnderIOItems.BasicCapacitor.get(1),
                EnderIOItems.EndermanHead.get(1))
            .fluidInputs(getFluid(Water, 2000))
            .itemOutputs(EnderIOItems.TormentedEndermanHead.get(1))
            .eut(500)
            .duration(1000)
            .addTo(GTN_Recipe.ImprovedSliceNSpliceRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                getPlate(Materials.Soularium, 2),
                getPlate(Materials.Silicon, 2),
                getPlate(Materials.VibrantAlloy, 1),
                EnderIOItems.EndermanHead.get(1))
            .itemOutputs(EnderIOItems.EnderResonator.get(1))
            .eut(500)
            .duration(2000)
            .addTo(GTN_Recipe.ImprovedSliceNSpliceRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                getPlate(Materials.Soularium, 2),
                getPlate(Materials.Silicon, 1),
                new ItemStack(Items.rotten_flesh, 2),
                new ItemStack(Items.skull, 1, 2))
            .itemOutputs(EnderIOItems.SkeletalContractor.get(1))
            .eut(500)
            .duration(2000)
            .addTo(GTN_Recipe.ImprovedSliceNSpliceRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                getPlate(Materials.EnergeticAlloy, 2),
                getPlate(Materials.Silicon, 1),
                new ItemStack(Items.diamond, 1),
                EnderIOItems.PulsatingCrystal.get(2))
            .itemOutputs(EnderIOItems.GuardianDiode.get(1))
            .eut(500)
            .duration(5000)
            .addTo(GTN_Recipe.ImprovedSliceNSpliceRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                getPlate(Materials.StellarAlloy, 2),
                getPlate(Materials.MelodicAlloy, 2),
                EnderIOItems.StellarCapacitor.get(1),
                EnderIOItems.SkeletalContractor.get(1))
            .itemOutputs(EnderIOItems.TotemicCapacitor.get(1))
            .eut(500)
            .duration(20000)
            .addTo(GTN_Recipe.ImprovedSliceNSpliceRecipes);
    }
}
