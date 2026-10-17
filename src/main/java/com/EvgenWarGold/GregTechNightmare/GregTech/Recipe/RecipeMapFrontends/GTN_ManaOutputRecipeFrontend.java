package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends;

import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.ItemManaDisplay;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData.SimpleMetaData;
import com.gtnewhorizons.modularui.api.math.Pos2d;

import codechicken.nei.PositionedStack;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.nei.GTNEIDefaultHandler;

/** Grid layout with the mana made by the recipe in the output slot. */
public class GTN_ManaOutputRecipeFrontend extends GTN_GridRecipeFrontend {

    public GTN_ManaOutputRecipeFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
    }

    @Override
    public void prepareRecipe(GTNEIDefaultHandler.CachedDefaultRecipe cachedRecipe) {
        int mana = cachedRecipe.mRecipe.getMetadataOrDefault(SimpleMetaData.MANA_OUTPUT, 0);
        if (mana <= 0 || cachedRecipe.mInputs.isEmpty()) return;
        // NEI positions are shifted from the layout positions, the first grid slot gives the shift
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        for (PositionedStack stack : cachedRecipe.mInputs) {
            minX = Math.min(minX, stack.relx);
            minY = Math.min(minY, stack.rely);
        }
        Pos2d manaPos = getItemOutputPositions(1).get(0);
        cachedRecipe.mOutputs.add(
            new GTNEIDefaultHandler.FixedPositionedStack(
                cachedRecipe,
                ItemManaDisplay.create(mana),
                false,
                manaPos.x + minX - LEFT,
                manaPos.y + minY - TOP));
    }
}
