package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps.AdvancedBBFRecipesPool;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps.AdvancedCokeOvenRecipesPool;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps.GTN_MultiBlockRecipes;

public class RecipeLoader {

    public static void init() {
        GTN_MultiBlockRecipes.init();
        AdvancedBBFRecipesPool.init();
        AdvancedCokeOvenRecipesPool.init();
    }
}
