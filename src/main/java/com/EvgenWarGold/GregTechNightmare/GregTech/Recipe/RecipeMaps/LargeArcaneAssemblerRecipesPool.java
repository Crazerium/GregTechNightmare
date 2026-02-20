package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;

import gregtech.api.enums.GTValues;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;

public class LargeArcaneAssemblerRecipesPool {

    public static void init() {

        for (Object obj : ThaumcraftApi.getCraftingRecipes()) {

            if (!(obj instanceof IArcaneRecipe)) continue;

            IArcaneRecipe recipe = (IArcaneRecipe) obj;

            ItemStack output = recipe.getRecipeOutput();
            if (output == null) continue;

            ItemStack[] inputs = convertRecipeInput(recipe);

            if (inputs.length == 0) continue;

            GTValues.RA.stdBuilder()
                .itemInputs(inputs)
                .itemOutputs(output.copy())
                .duration(200)
                .eut(30)
                .addTo(GTN_Recipe.ARCANE_ASSEMBLER_RECIPES);
        }
    }

    private static ItemStack[] convertRecipeInput(IArcaneRecipe recipe) {

        List<ItemStack> result = new ArrayList<>();

        if (recipe instanceof ShapedArcaneRecipe) {

            ShapedArcaneRecipe shaped = (ShapedArcaneRecipe) recipe;

            Object[] raw = shaped.getInput();

            for (Object obj : raw) {
                addIngredient(result, obj);
            }
        }

        else if (recipe instanceof ShapelessArcaneRecipe) {

            ShapelessArcaneRecipe shapeless = (ShapelessArcaneRecipe) recipe;

            for (Object obj : shapeless.getInput()) {
                addIngredient(result, obj);
            }
        }

        return result.toArray(new ItemStack[0]);
    }

    private static void addIngredient(List<ItemStack> result, Object obj) {

        if (obj == null) return;

        if (obj instanceof ItemStack) {
            result.add(((ItemStack) obj).copy());
        }

        else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            if (!list.isEmpty() && list.get(0) instanceof ItemStack) result.add(((ItemStack) list.get(0)).copy());
        }
    }
}
