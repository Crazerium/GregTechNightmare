package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;

public class GTN_Recipe {

    public static final RecipeMap<RecipeMapBackend> AdvancedBBFRecipes = RecipeMapBuilder
        .of("gtn.recipe.AdvancedBBFRecipes")
        .maxIO(1, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .build();
}
