package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends;

import java.util.ArrayList;
import java.util.List;

import com.EvgenWarGold.GregTechNightmare.GregTech.Mana.ItemManaDisplay;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData.SimpleMetaData;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;

import codechicken.nei.PositionedStack;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.nei.GTNEIDefaultHandler;

/**
 * NEI layout of the Botania machines: a grid of item inputs, then the recipe mana as an extra input slot. Moving the
 * recipe into a pattern terminal turns that slot into mana, so the pattern always carries the exact mana of one craft.
 */
public class GTN_ManaRecipeFrontend extends RecipeMapFrontend {

    private static final int SLOT = 18;
    private static final int LEFT = 8;
    private static final int TOP = 8;

    public GTN_ManaRecipeFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
    }

    /** The last input slot is the mana slot. */
    private int getItemSlots() {
        return uiProperties.maxItemInputs - 1;
    }

    /**
     * Item grid width: one row for up to 3 items, otherwise 4 columns. Computed from the slot count because GT calls
     * {@link #modifyNEIProperties} from the super constructor, before fields of this class are set.
     */
    private int getColumns() {
        int itemSlots = getItemSlots();
        return itemSlots <= 3 ? Math.max(itemSlots, 1) : 4;
    }

    private int getRows() {
        return (getItemSlots() + getColumns() - 1) / getColumns();
    }

    @Override
    protected NEIRecipePropertiesBuilder modifyNEIProperties(NEIRecipePropertiesBuilder builder) {
        int height = Math.max(TOP + getRows() * SLOT, TOP + 2 * SLOT) + 18;
        return builder.recipeBackgroundSize(new Size(170, height));
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        List<Pos2d> result = new ArrayList<>(itemInputCount);
        int itemSlots = getItemSlots();
        for (int i = 0; i < itemInputCount; i++) {
            if (i < itemSlots) {
                result.add(new Pos2d(LEFT + (i % getColumns()) * SLOT, TOP + (i / getColumns()) * SLOT));
            } else {
                result.add(getManaSlotPosition());
            }
        }
        return result;
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        List<Pos2d> result = new ArrayList<>(itemOutputCount);
        for (int i = 0; i < itemOutputCount; i++) result.add(new Pos2d(138, getMiddleY() + i * SLOT));
        return result;
    }

    /** Right of the item grid, in its middle row. */
    private Pos2d getManaSlotPosition() {
        return new Pos2d(LEFT + getColumns() * SLOT + 6, getMiddleY());
    }

    private int getMiddleY() {
        return TOP + (Math.max(getRows(), 1) - 1) * SLOT / 2;
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
        Pos2d manaPos = getManaSlotPosition();
        cachedRecipe.mInputs.add(
            new GTNEIDefaultHandler.FixedPositionedStack(
                cachedRecipe,
                ItemManaDisplay.create(mana),
                false,
                manaPos.x + minX - LEFT,
                manaPos.y + minY - TOP));
    }
}
