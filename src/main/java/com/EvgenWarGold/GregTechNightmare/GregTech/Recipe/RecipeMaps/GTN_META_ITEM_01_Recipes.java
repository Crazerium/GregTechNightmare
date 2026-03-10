package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_InventoryUtils.createItem;
import static com.EvgenWarGold.GregTechNightmare.Utils.GTN_OreDict.getWireGt16;
import static gregtech.api.enums.TierEU.RECIPE_IV;
import static gregtech.api.enums.TierEU.RECIPE_LuV;
import static gregtech.api.util.GTModHandler.addCraftingRecipe;

import com.EvgenWarGold.GregTechNightmare.ModBlocks.ModBlocks;
import com.EvgenWarGold.GregTechNightmare.ModItems.ModItems;
import goodgenerator.util.ItemRefer;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMaps;
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

        // Meteor Miner Schematic Tier I
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Shape_Empty.get(64),
                ItemList.MiningDroneHV.get(1),
                ModItems.BOTANIA_ITEMS.ManaLensBore.get(1),
                ItemList.Emitter_IV.get(8),
                ItemList.Sensor_IV.get(8)
            )
            .itemOutputs(GTN_ItemList.MeteorMinerSchematic1.get(1))

            .eut(RECIPE_IV)
            .duration(20 * 60)
            .addTo(RecipeMaps.assemblerRecipes);

        // Meteor Miner Schematic Tier II
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTN_ItemList.MeteorMinerSchematic1.get(1),
                ItemList.MiningDroneLuV.get(1),
                ModItems.BLOOD_MAGIC_ITEMS.LifeShard.get(16),
                ModItems.BLOOD_MAGIC_ITEMS.SoulShard.get(16),
                ItemList.Emitter_LuV.get(8),
                ItemList.Sensor_LuV.get(8)
            )
            .itemOutputs(GTN_ItemList.MeteorMinerSchematic2.get(1))

            .eut(RECIPE_LuV)
            .duration(20 * 60)
            .addTo(RecipeMaps.assemblerRecipes);

        //spotless:on
    }
}
