package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;

import javax.annotation.Nullable;

import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.nei.RecipeDisplayInfo;

/** Ticks of work after which one flower of the recipe withers and is used up. */
public final class GTN_FlowerLifetimeMetadataKey extends RecipeMetadataKey<Integer> {

    public GTN_FlowerLifetimeMetadataKey() {
        super(Integer.class, "flower_lifetime");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        int ticks = cast(value, 0);
        if (ticks <= 0) return;
        recipeInfo.drawText(GTN_Utils.tr("gtn.recipe.utils.flower_lifetime", formatNumber(ticks / 20)));
    }
}
