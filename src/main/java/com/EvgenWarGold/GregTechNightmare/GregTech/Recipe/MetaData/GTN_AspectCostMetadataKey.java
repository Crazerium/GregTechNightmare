package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData;

import javax.annotation.Nullable;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends.GTN_EssentiaRecipeFrontend;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.nei.RecipeDisplayInfo;
import thaumcraft.api.aspects.AspectList;

public final class GTN_AspectCostMetadataKey extends RecipeMetadataKey<AspectList> {

    public GTN_AspectCostMetadataKey() {
        super(AspectList.class, "aspect_cost");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        AspectList aspects = cast(value);
        if (aspects == null || aspects.size() == 0) return;
        for (String tag : GTN_EssentiaRecipeFrontend.getMissingAspectTags(aspects)) {
            recipeInfo.drawText("Aspect icon unavailable: " + tag, 9);
        }
    }
}
