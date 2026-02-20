package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.createItem;
import static gregtech.api.util.GTModHandler.addCraftingRecipe;

import net.minecraft.init.Blocks;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;

public class GTN_META_ITEM_01_Recipes {

    public static void init() {
        // spotless:off

        // Advanced Clay
        addCraftingRecipe(
            GTN_ItemList.AdvancedClay.get(1),
            new Object[]{"AAA", "BCB", "EEE",
                'A', createItem(Blocks.sand),
                'B', createItem(Blocks.dirt),
                'E', createItem(Blocks.gravel),
                'C', createItem(Blocks.clay)
            });

        //spotless:on
    }
}
