package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.EV.GTN_ElvenGateway;
import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.UV.GTN_LargeBioLab;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends.GTN_EssentiaRecipeFrontend;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends.GTN_GridRecipeFrontend;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends.GTN_ManaOutputRecipeFrontend;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends.GTN_ManaRecipeFrontend;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends.GasCollectorRecipesFrontend;
import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMaps.GTN_BotaniaRecipesPool;

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
        .maxIO(1, 1, 0, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .build();
    public static final RecipeMap<?> IndustrialCrucibleRecipes = RecipeMapBuilder
        .of("gtn.recipe.IndustrialCrucibleRecipes")
        .maxIO(17, 1, 0, 0)
        .minInputs(1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend((ui, nei) -> new GTN_EssentiaRecipeFrontend(ui, nei, false))
        .build();

    public static final RecipeMap<?> AutomatedInfusionRecipes = RecipeMapBuilder
        .of("gtn.recipe.AutomatedInfusionRecipes")
        .maxIO(GTN_EssentiaRecipeFrontend.INFUSION_ITEM_SLOTS + GTN_EssentiaRecipeFrontend.MAX_ASPECT_SLOTS, 1, 0, 0)
        .minInputs(1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .progressBarPos(120, 39)
        .frontend((ui, nei) -> new GTN_EssentiaRecipeFrontend(ui, nei, true))
        .build();

    public static final RecipeMap<?> ManaInfusionRecipes = RecipeMapBuilder.of("gtn.recipe.ManaInfusionRecipes")
        .maxIO(3, 1, 0, 0)
        .minInputs(1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .progressBarPos(110, 8)
        .frontend(GTN_ManaRecipeFrontend::new)
        .build();

    public static final RecipeMap<?> RuneAltarRecipes = RecipeMapBuilder.of("gtn.recipe.RuneAltarRecipes")
        .maxIO(GTN_BotaniaRecipesPool.RUNE_ALTAR_ITEM_SLOTS + 1, 1, 0, 0)
        .minInputs(1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .progressBarPos(110, 26)
        .frontend(GTN_ManaRecipeFrontend::new)
        .build();

    public static final RecipeMap<?> TerraPlateRecipes = RecipeMapBuilder.of("gtn.recipe.TerraPlateRecipes")
        .maxIO(4, 1, 0, 0)
        .minInputs(1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .progressBarPos(110, 8)
        .frontend(GTN_ManaRecipeFrontend::new)
        .build();

    public static final RecipeMap<?> PureDaisyRecipes = RecipeMapBuilder.of("gtn.recipe.PureDaisyRecipes")
        .maxIO(1, 1, 0, 0)
        .minInputs(1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .progressBarPos(110, GTN_GridRecipeFrontend.getProgressBarY(1))
        .frontend(GTN_GridRecipeFrontend::new)
        .build();

    public static final RecipeMap<?> PetalApothecaryRecipes = RecipeMapBuilder.of("gtn.recipe.PetalApothecaryRecipes")
        .maxIO(GTN_BotaniaRecipesPool.PETAL_APOTHECARY_ITEM_SLOTS, 1, 1, 0)
        .minInputs(1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .progressBarPos(110, GTN_GridRecipeFrontend.getProgressBarY(GTN_BotaniaRecipesPool.PETAL_APOTHECARY_ITEM_SLOTS))
        .frontend(GTN_GridRecipeFrontend::new)
        .build();

    public static final RecipeMap<?> ElvenTradeRecipes = RecipeMapBuilder.of("gtn.recipe.ElvenTradeRecipes")
        .maxIO(GTN_BotaniaRecipesPool.ELVEN_TRADE_ITEM_SLOTS, 1, 0, 0)
        .minInputs(1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .progressBarPos(110, GTN_GridRecipeFrontend.getProgressBarY(GTN_BotaniaRecipesPool.ELVEN_TRADE_ITEM_SLOTS))
        .neiSpecialInfoFormatter(GTN_ElvenGateway::getRecipeInfo)
        .frontend(GTN_GridRecipeFrontend::new)
        .build();

    public static final RecipeMap<?> GreenhouseRecipes = RecipeMapBuilder.of("gtn.recipe.GreenhouseRecipes")
        .maxIO(2, 1, 1, 0)
        .minInputs(1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .progressBarPos(110, GTN_GridRecipeFrontend.getProgressBarY(2))
        .frontend(GTN_ManaOutputRecipeFrontend::new)
        .build();

    public static final RecipeMap<?> ArcaneAssemblerRecipes = RecipeMapBuilder
        .of("gtn.recipe.LargeArcaneAssemblerRecipes")
        .maxIO(9, 1, 0, 0)
        .minInputs(1, 0)
        .build();
    public static final RecipeMap<?> GasCollectorRecipes = RecipeMapBuilder.of("gtn.recipe.GasCollectorRecipes")
        .maxIO(1, 0, 0, 11)
        .minInputs(1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GasCollectorRecipesFrontend::new)
        .build();
    public static final RecipeMap<?> VacuumNukeRecipes = RecipeMapBuilder.of("gtn.recipe.VacuumNukeRecipes")
        .maxIO(1, 1, 1, 1)
        .minInputs(1, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .build();

    public static final RecipeMap<?> ImprovedAlgaeFarmRecipes = RecipeMapBuilder
        .of("gtn.recipe.ImprovedAlgaeFarmRecipes")
        .maxIO(1, 5, 0, 0)
        .minInputs(0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .build();
    public static final RecipeMap<?> ImprovedSliceNSpliceRecipes = RecipeMapBuilder
        .of("gtn.recipe.ImprovedSliceNSpliceRecipes")
        .maxIO(6, 1, 1, 0)
        .minInputs(0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW)
        .build();

    public static final RecipeMap<?> LargeBioLabRecipes = RecipeMapBuilder.of("gtn.recipe.LargeBioLabRecipes")
        .maxIO(6, 1, 6, 0)
        .minInputs(1, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .neiSpecialInfoFormatter(GTN_LargeBioLab::getRecipeInfo)
        .build();
}
