package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends.CokeOvenRecipesFrontend;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;

public class GTN_Recipe {

    public static final RecipeMap<RecipeMapBackend> AdvancedBBFRecipes = RecipeMapBuilder
        .of("gtn.recipe.AdvancedBBFRecipes")
        .maxIO(2, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .build();

    public static final RecipeMap<RecipeMapBackend> CokeOvenRecipes = RecipeMapBuilder
        .of("gtn.recipe.AdvancedCokeOvenRecipes")
        .maxIO(0, 1, 0, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(CokeOvenRecipesFrontend::new)
        .useSpecialSlot()
        .specialSlotSensitive()
        .build();
    public static final RecipeMap<?> ARCANE_ASSEMBLER_RECIPES = RecipeMapBuilder.of("gtn.arcaneassembler")
        .maxIO(9, 1, 0, 0)
        .minInputs(1, 0)
        .build();

}
