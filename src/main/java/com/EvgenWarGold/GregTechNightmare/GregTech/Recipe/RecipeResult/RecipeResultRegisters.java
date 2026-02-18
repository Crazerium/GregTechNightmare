package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeResult;

import gregtech.api.recipe.check.CheckRecipeResultRegistry;

public class RecipeResultRegisters {

    public static void init() {
        CheckRecipeResultRegistry.register(new ResultInsufficientRangeTier(0, 0));
    }
}
