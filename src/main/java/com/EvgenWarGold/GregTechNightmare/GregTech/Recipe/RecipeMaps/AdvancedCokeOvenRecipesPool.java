package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.objects.OreDictItemStack;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.items.RailcraftToolItems;

public class AdvancedCokeOvenRecipesPool {

    public static void init() {
        if (Mods.Railcraft.isModLoaded()) {
            GTValues.RA.stdBuilder()
                .itemInputs(Materials.Coal.getGems(1))
                .itemOutputs(RailcraftToolItems.getCoalCoke(1))
                .fluidOutputs(Materials.Creosote.getFluid(500))
                .duration(45 * SECONDS)
                .eut(0)
                .addTo(GTN_Recipe.CokeOvenRecipes);

            GTValues.RA.stdBuilder()
                .itemInputs(Materials.Coal.getBlocks(1))
                .itemOutputs(EnumCube.COKE_BLOCK.getItem(1))
                .fluidOutputs(Materials.Creosote.getFluid(4_500))
                .duration(45 * SECONDS)
                .eut(0)
                .addTo(GTN_Recipe.CokeOvenRecipes);
        }

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.cactus))
            .itemOutputs(GregtechItemList.CactusCharcoal.get(1))
            .fluidOutputs(Materials.Creosote.getFluid(30))
            .duration(SECONDS)
            .eut(0)
            .addTo(GTN_Recipe.CokeOvenRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.CactusCharcoal.get(1))
            .itemOutputs(GregtechItemList.CactusCoke.get(1))
            .fluidOutputs(Materials.Creosote.getFluid(30))
            .duration(SECONDS)
            .eut(0)
            .addTo(GTN_Recipe.CokeOvenRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.reeds))
            .itemOutputs(GregtechItemList.SugarCharcoal.get(1))
            .fluidOutputs(Materials.Creosote.getFluid(30))
            .duration(SECONDS)
            .eut(0)
            .addTo(GTN_Recipe.CokeOvenRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.SugarCharcoal.get(1))
            .itemOutputs(GregtechItemList.SugarCoke.get(1))
            .fluidOutputs(Materials.Creosote.getFluid(30))
            .duration(SECONDS)
            .eut(0)
            .addTo(GTN_Recipe.CokeOvenRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.WoodPellet.get(2))
            .itemOutputs(Materials.Charcoal.getGems(3))
            .duration(5 * SECONDS)
            .eut(0)
            .addTo(GTN_Recipe.CokeOvenRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(new OreDictItemStack("logWood", 1))
            .itemOutputs(Materials.Charcoal.getGems(1))
            .fluidOutputs(Materials.Creosote.getFluid(250))
            .duration(5 * SECONDS)
            .eut(0)
            .addTo(GTN_Recipe.CokeOvenRecipes);
    }
}
