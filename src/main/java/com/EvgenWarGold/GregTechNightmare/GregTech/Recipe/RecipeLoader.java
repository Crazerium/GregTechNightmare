package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps.AdvancedBBFRecipesPool;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps.AdvancedCokeOvenRecipesPool;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps.GTN_META_BLOCK_CASING_01_Recipes;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps.GTN_META_ITEM_01_Recipes;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps.GTN_MultiBlockRecipes;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps.LargeArcaneAssemblerRecipesPool;

public class RecipeLoader {

    public static void init() {
        GTN_MultiBlockRecipes.init();
        GTN_META_ITEM_01_Recipes.init();
        GTN_META_BLOCK_CASING_01_Recipes.init();
        AdvancedBBFRecipesPool.init();
        AdvancedCokeOvenRecipesPool.init();
        LargeArcaneAssemblerRecipesPool.init();
    }
}
