package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends;

import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.ItemManaDisplay;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData.SimpleMetaData;
import com.gtnewhorizons.modularui.api.math.Pos2d;

import codechicken.nei.PositionedStack;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.nei.GTNEIDefaultHandler;

/**
 * Grid layout with the recipe mana in the side slot. Moving the recipe into a pattern terminal turns that slot into
 * mana, so the pattern always carries the exact mana of one craft.
 */
public class GTN_ManaRecipeFrontend extends GTN_GridRecipeFrontend {

    public GTN_ManaRecipeFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
    }

    /** The last input slot is the mana slot. */
    @Override
    protected int getExtraItemSlots() {
        return 1;
    }

    /**
     * Adds the recipe mana as an input stack. Done here instead of an inputs getter because GT shows ore dictionary
     * recipes from their alternatives and skips the getter for them.
     */
    @Override
    public void prepareRecipe(GTNEIDefaultHandler.CachedDefaultRecipe cachedRecipe) {
        int mana = cachedRecipe.mRecipe.getMetadataOrDefault(SimpleMetaData.MANA_COST, 0);
        if (mana <= 0 || cachedRecipe.mInputs.isEmpty()) return;
        // NEI positions are shifted from the layout positions, the first grid slot gives the shift
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        for (PositionedStack stack : cachedRecipe.mInputs) {
            minX = Math.min(minX, stack.relx);
            minY = Math.min(minY, stack.rely);
        }
        Pos2d manaPos = getSideSlotPosition();
        cachedRecipe.mInputs.add(
            new GTNEIDefaultHandler.FixedPositionedStack(
                cachedRecipe,
                ItemManaDisplay.create(mana),
                false,
                manaPos.x + minX - LEFT,
                manaPos.y + minY - TOP));
    }
}
