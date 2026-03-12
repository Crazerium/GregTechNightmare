package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.util.GTModHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class GasCollectorRecipesPool {
    private static ItemStack dimensionToken(String id) {
        ItemStack stack = GTModHandler.getModItem("gtneioreplugin", id, 1);
        if (stack != null) stack.stackSize = 0;
        return stack;
    }
    private static FluidStack gas(Materials mat, int amount) {
        return mat.getGas(amount);
    }
    private static FluidStack fluid(String id, int amount) {
        return new FluidStack(FluidRegistry.getFluid(id), amount);
    }
    private static void addPlanet(String suffix, int eut, int duration, FluidStack... fluids) {
        ItemStack token = dimensionToken("blockDimensionDisplay_" + suffix);
        if (token == null) return;
        GTValues.RA.stdBuilder()
            .itemInputs(token)
            .fluidOutputs(fluids)
            .duration(duration)
            .eut(eut)
            .addTo(GTN_Recipe.GasCollectorRecipes);
    }
    private static void addPlanet(String suffix, int eut, int duration, ItemStack[] items, FluidStack... fluids) {
        ItemStack token = dimensionToken("blockDimensionDisplay_" + suffix);
        if (token == null) return;
        GTValues.RA.stdBuilder()
            .itemInputs(token)
            .fluidOutputs(fluids)
            .duration(duration)
            .eut(eut)
            .addTo(GTN_Recipe.GasCollectorRecipes);
    }
    public static void init() {
        addPlanet("Ow", 120, 600,
            gas(Materials.Nitrogen, 78000),
            gas(Materials.Oxygen, 20000),
            gas(Materials.Argon, 1000),
            gas(Materials.CarbonDioxide, 100)
        );
        addPlanet("Ne", 8192, 400,
            fluid("nefariousgas", 8000),
            fluid("fluid.coalgas", 16000),
            fluid("fluid.anthracene", 66000),
            gas(Materials.SulfurTrioxide, 210000),
            gas(Materials.SulfurDioxide,380000),
            gas(Materials.NitrogenDioxide,140000),
            fluid("neon", 36000)
        );
    }
}
