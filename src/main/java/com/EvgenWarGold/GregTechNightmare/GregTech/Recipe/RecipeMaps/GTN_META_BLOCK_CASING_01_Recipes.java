package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import net.minecraft.init.Blocks;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.createItem;
import static gregtech.api.util.GTModHandler.addCraftingRecipe;
import static gregtech.api.util.GTModHandler.addShapelessCraftingRecipe;

public class GTN_META_BLOCK_CASING_01_Recipes {

    public static void init() {
        //spotless:off

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
