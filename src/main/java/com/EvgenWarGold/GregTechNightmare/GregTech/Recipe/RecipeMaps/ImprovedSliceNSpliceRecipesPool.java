package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict.getFluid;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict.getplate;
import static gregtech.api.enums.Materials.Water;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import com.EvgenWarGold.GregTechNightmare.ModItems.ModItems;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;

public class ImprovedSliceNSpliceRecipesPool {

    public static void init() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                getplate(Materials.EnergeticAlloy, 2),
                getplate(Materials.Silicon, 2),
                ModItems.ENDER_IO.BasicCapacitor.get(1),
                new ItemStack(Items.skull, 1, 2))
            .itemOutputs(ModItems.ENDER_IO.ZombieElectrode.get(1))
            .eut(500)
            .duration(1000)
            .addTo(GTN_Recipe.ImprovedSliceNSpliceRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                getplate(Materials.Soularium, 2),
                getplate(Materials.Silicon, 2),
                getplate(Materials.RedAlloy, 1),
                new ItemStack(Items.skull, 1, 2))
            .itemOutputs(ModItems.ENDER_IO.ZLogicCapacitor.get(1))
            .eut(500)
            .duration(2000)
            .addTo(GTN_Recipe.ImprovedSliceNSpliceRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                getplate(Materials.Soularium, 2),
                ModItems.ENDER_IO.BasicCapacitor.get(1),
                ModItems.ENDER_IO.EndermanHead.get(1))
            .fluidInputs(getFluid(Water, 2000))
            .itemOutputs(ModItems.ENDER_IO.TormentedEndermanHead.get(1))
            .eut(500)
            .duration(1000)
            .addTo(GTN_Recipe.ImprovedSliceNSpliceRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                getplate(Materials.Soularium, 2),
                getplate(Materials.Silicon, 2),
                getplate(Materials.VibrantAlloy, 1),
                ModItems.ENDER_IO.EndermanHead.get(1))
            .itemOutputs(ModItems.ENDER_IO.EnderResonator.get(1))
            .eut(500)
            .duration(2000)
            .addTo(GTN_Recipe.ImprovedSliceNSpliceRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                getplate(Materials.Soularium, 2),
                getplate(Materials.Silicon, 1),
                new ItemStack(Items.rotten_flesh, 2),
                new ItemStack(Items.skull, 1, 2))
            .itemOutputs(ModItems.ENDER_IO.SkeletalContractor.get(1))
            .eut(500)
            .duration(2000)
            .addTo(GTN_Recipe.ImprovedSliceNSpliceRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                getplate(Materials.EnergeticAlloy, 2),
                getplate(Materials.Silicon, 1),
                new ItemStack(Items.diamond, 1),
                ModItems.ENDER_IO.PulsatingCrystal.get(2))
            .itemOutputs(ModItems.ENDER_IO.GuardianDiode.get(1))
            .eut(500)
            .duration(5000)
            .addTo(GTN_Recipe.ImprovedSliceNSpliceRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(
                getplate(Materials.StellarAlloy, 2),
                getplate(Materials.MelodicAlloy, 2),
                ModItems.ENDER_IO.StellarCapacitor.get(1),
                ModItems.ENDER_IO.SkeletalContractor.get(1))
            .itemOutputs(ModItems.ENDER_IO.TotemicCapacitor.get(1))
            .eut(500)
            .duration(20000)
            .addTo(GTN_Recipe.ImprovedSliceNSpliceRecipes);
    }
}
