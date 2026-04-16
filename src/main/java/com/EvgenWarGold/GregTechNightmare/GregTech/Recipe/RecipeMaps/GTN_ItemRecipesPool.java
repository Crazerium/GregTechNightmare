package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict.getDense;
import static gregtech.api.util.GTModHandler.addCraftingRecipe;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;
import com.EvgenWarGold.GregTechNightmare.ModBlocks.ModBlocks;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsBotania;

public class GTN_ItemRecipesPool {

    public static void init() {
        // spotless:off
        // Mana Prospector
        addCraftingRecipe(
            GTN_ItemList.ManaProspector.get(1),
            new Object[]{"ABC", "GDG", "FWE",
                'A', Materials.LightFuel.getCells(1),
                'B', getDense(MaterialsBotania.Manasteel),
                'C', Materials.Benzene.getCells(1),
                'G', getDense(Materials.RedstoneAlloy),
                'D', ModBlocks.BOTANIA_BLOCKS.ManaPool.getItemStack(1),
                'F', ItemList.Sensor_LV.get(1),
                'W', getDense(Materials.Thaumium),
                'E', ItemList.Emitter_LV.get(1)
            });
        // spotless:on
    }
}
