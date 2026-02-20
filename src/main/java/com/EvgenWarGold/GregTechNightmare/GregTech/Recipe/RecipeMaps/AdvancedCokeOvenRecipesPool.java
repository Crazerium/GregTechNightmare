package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.GTN_Recipe;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;

public class AdvancedCokeOvenRecipesPool {

    static ItemStack[] allWoodWithTag;

    public static void init() {
        ArrayList<ItemStack> allWoodInCopy = new ArrayList<>();

        for (ItemStack itemStack : OreDictionary.getOres("logWood")) {
            if (itemStack != null) {
                allWoodInCopy.add(itemStack.copy());
            }
        }

        allWoodWithTag = allWoodInCopy.toArray(new ItemStack[0]);

        GTValues.RA.stdBuilder()
            .itemOutputs(Materials.Charcoal.getGems(1))
            .fluidOutputs(Materials.Creosote.getFluid(250))
            .special(allWoodWithTag)
            .fake()
            .duration(20)
            .eut(0)
            .fake()
            .addTo(GTN_Recipe.CokeOvenRecipes);
    }
}
