package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizons.modularui.api.math.Pos2d;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;

public class CokeOvenRecipesFrontend extends RecipeMapFrontend {

    public CokeOvenRecipesFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
    }

    @Override
    public @NotNull Pos2d getSpecialItemPosition() {
        return new Pos2d(52, 24);
    }

    @Override
    public @NotNull List<Pos2d> getFluidOutputPositions(int fluidOutputCount) {
        return Arrays.asList(new Pos2d(127, 24));
    }
}
