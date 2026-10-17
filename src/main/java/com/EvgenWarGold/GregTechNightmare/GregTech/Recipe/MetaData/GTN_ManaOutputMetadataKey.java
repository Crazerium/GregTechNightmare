package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;

import javax.annotation.Nullable;

import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.nei.RecipeDisplayInfo;

/** Mana made once per craft. */
public final class GTN_ManaOutputMetadataKey extends RecipeMetadataKey<Integer> {

    public GTN_ManaOutputMetadataKey() {
        super(Integer.class, "mana_output");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        int mana = cast(value, 0);
        if (mana <= 0) return;
        recipeInfo.drawText(GTN_Utils.tr("gtn.recipe.utils.mana_output", formatNumber(mana)));
    }
}
