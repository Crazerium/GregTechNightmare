package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static gregtech.api.util.GTModHandler.addShapelessCraftingRecipe;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;

public class GTN_META_BLOCK_CASING_01_Recipes {

    public static void init() {
        // spotless:off

        // Coke Oven Casing
        addShapelessCraftingRecipe(
            GTN_ItemList.CokeOvenCasing.get(1),
            new Object[]{ GTN_ItemList.AdvancedClay.get(1),
                GTN_ItemList.AdvancedClay.get(1),
                GTN_ItemList.AdvancedClay.get(1),
                GTN_ItemList.AdvancedClay.get(1)});

        //spotless:on
    }
}
