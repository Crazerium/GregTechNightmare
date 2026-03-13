package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;

public class GasCollectorRecipesFrontend extends RecipeMapFrontend {

    public GasCollectorRecipesFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
    }
    @Override
    public @NotNull List<Pos2d> getFluidOutputPositions(int fluidOutputCount) {
        List<Pos2d> list = new ArrayList<>();
        int startX = 100;
        int startY = 10;
        int columns = 3;
        for (int i = 0; i < fluidOutputCount; i++) {
            int x = startX + (i % columns) * 18;
            int y = startY + (i / columns) * 18;
            list.add(new Pos2d(x, y));
        }
        return list;
    }
    @Override
    public @NotNull Pos2d getSpecialItemPosition() {
        return new Pos2d(50, 25);
    }
}
