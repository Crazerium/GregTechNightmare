package com.EvgenWarGold.GregTechNightmare.DeleteRecipe;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.util.GTRecipe;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeleteRecipeUtils {

    public static void removeRecipeByOutputFromMap(RecipeMap<?> recipeMap, ItemStack output) {
        if (recipeMap == null || output == null || output.getItem() == null) {
            return;
        }

        RecipeMapBackend backend = recipeMap.getBackend();

        Collection<GTRecipe> allRecipes = backend.getAllRecipes();

        List<GTRecipe> recipesToRemove = new ArrayList<>();

        for (GTRecipe recipe : allRecipes) {
            if (recipe == null || recipe.mOutputs == null) {
                continue;
            }

            for (ItemStack stack : recipe.mOutputs) {
                if (stack != null && stack.isItemEqual(output)) {
                    recipesToRemove.add(recipe);
                    break;
                }
            }
        }

        if (!recipesToRemove.isEmpty()) {
            backend.removeRecipes(recipesToRemove);
        }
    }

    public static void removeRecipesByOutput(ItemStack output) {
        if (output == null || output.getItem() == null) {
            return;
        }

        for (RecipeMap<?> recipeMap : RecipeMap.ALL_RECIPE_MAPS.values()) {
            if (recipeMap == null) {
                continue;
            }

            removeRecipeByOutputFromMap(recipeMap, output);
        }
    }

    public static void removeRecipesByOutputDirect(RecipeMapBackend backend, ItemStack output) {
        if (backend == null || output == null) return;

        List<GTRecipe> toRemove = new ArrayList<>();

        for (GTRecipe recipe : backend.getAllRecipes()) {
            if (recipe.mOutputs != null) {
                for (ItemStack stack : recipe.mOutputs) {
                    if (stack != null && stack.isItemEqual(output)) {
                        toRemove.add(recipe);
                        break;
                    }
                }
            }
        }

        if (!toRemove.isEmpty()) {
            backend.removeRecipes(toRemove);
        }
    }
}
