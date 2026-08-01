package com.EvgenWarGold.GregTechNightmare.DeleteRecipe;

import com.EvgenWarGold.GregTechNightmare.ModItems.AE2FluidCraftItems;

public class DeleteRecipe {

    public static void init() {
        DeleteRecipeUtils.removeRecipesByOutput(AE2FluidCraftItems.SuperStockReplenisher.get(1));
    }
}
