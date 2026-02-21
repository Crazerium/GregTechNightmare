package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import java.util.ArrayList;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses.OverclockType;
import net.minecraft.item.ItemStack;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;
import gregtech.api.enums.GTValues;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
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
            String researchKey = recipe.getResearch();
            if (inputs.length == 0) continue;
            AspectList aspects = recipe.getAspects();
            GTValues.RA.stdBuilder()
                .itemInputs(inputs)
                .itemOutputs(output.copy())
                .duration(20)
                .eut(2048)
                .metadata(GTN_Recipe.ASPECT_COST, aspects)
                .metadata(GTN_Recipe.RESEARCH_KEY, researchKey)
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
