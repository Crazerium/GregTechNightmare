package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static gregtech.api.util.GTModHandler.addCraftingRecipe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;

public class GTN_MultiBlockRecipes {

    public static void init() {
        // spotless:off

        // Advanced BBF
        addCraftingRecipe(
            GTN_ItemList.AdvancedBBF.get(1),
            new Object[]{"ABA", "BCB", "BBB",
                'A', OrePrefixes.frameGt.get(Materials.Steel),
                'B', ItemList.Casing_Firebricks,
                'C', ItemList.Machine_Bricked_BlastFurnace
            });

        // Bronze Void Miner
        addCraftingRecipe(
            GTN_ItemList.BronzeVoidMiner.get(1),
            new Object[]{"ABA", "BCB", "EDE",
                'A', OrePrefixes.frameGt.get(Materials.Bronze),
                'B', ItemList.Casing_BronzePlatedBricks,
                'D', ItemList.Machine_Bronze_Boiler.get(1),
                'C', new ItemStack(Items.diamond_pickaxe),
                'E', ItemList.Casing_Firebox_Bronze
            });
        //spotless:on
    }
}
