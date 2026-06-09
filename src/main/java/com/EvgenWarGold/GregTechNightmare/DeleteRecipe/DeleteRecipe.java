package com.EvgenWarGold.GregTechNightmare.DeleteRecipe;

import com.EvgenWarGold.GregTechNightmare.ModItems.ModItems;

public class DeleteRecipe {
    public static void init() {
        DeleteRecipeUtils.removeRecipesByOutput(ModItems.AE_2_FLUID_CRAFT_ITEMS.SuperStockReplenisher.get(1));
    }
}
