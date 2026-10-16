package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends;

import java.util.ArrayList;
import java.util.List;

import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;

/**
 * NEI layout of the Botania machines: a grid of item inputs, a side slot right of the grid (fluid input or mana), then
 * the output. One row for up to 3 items, otherwise 4 columns.
 */
public class GTN_GridRecipeFrontend extends RecipeMapFrontend {

    protected static final int SLOT = 18;
    protected static final int LEFT = 8;
    protected static final int TOP = 8;

    public GTN_GridRecipeFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
    }

    /**
     * Item input slots that are not part of the grid. Methods instead of fields because GT calls
     * {@link #modifyNEIProperties} from the super constructor, before fields of subclasses are set.
     */
    protected int getExtraItemSlots() {
        return 0;
    }

    protected int getItemSlots() {
        return uiProperties.maxItemInputs - getExtraItemSlots();
    }

    private int getColumns() {
        int itemSlots = getItemSlots();
        return itemSlots <= 3 ? Math.max(itemSlots, 1) : 4;
    }

    private int getRows() {
        return (getItemSlots() + getColumns() - 1) / getColumns();
    }

    private int getMiddleY() {
        return TOP + (getRows() - 1) * SLOT / 2;
    }

    /** Right of the item grid, in its middle row. */
    protected Pos2d getSideSlotPosition() {
        return new Pos2d(LEFT + getColumns() * SLOT + 6, getMiddleY());
    }

    /** Progress bar position for this layout, see {@link gregtech.api.recipe.RecipeMapBuilder#progressBarPos}. */
    public static int getProgressBarY(int itemSlots) {
        int rows = itemSlots <= 3 ? 1 : (itemSlots + 3) / 4;
        return TOP + (rows - 1) * SLOT / 2;
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
                result.add(getSideSlotPosition());
            }
        }
        return result;
    }

    @Override
    public List<Pos2d> getFluidInputPositions(int fluidInputCount) {
        List<Pos2d> result = new ArrayList<>(fluidInputCount);
        Pos2d side = getSideSlotPosition();
        for (int i = 0; i < fluidInputCount; i++) result.add(new Pos2d(side.x, side.y + i * SLOT));
        return result;
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        List<Pos2d> result = new ArrayList<>(itemOutputCount);
        for (int i = 0; i < itemOutputCount; i++) result.add(new Pos2d(138, getMiddleY() + i * SLOT));
        return result;
    }
}
