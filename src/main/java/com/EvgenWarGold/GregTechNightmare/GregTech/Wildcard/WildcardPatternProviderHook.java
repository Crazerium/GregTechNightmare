package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_WildcardPatternBuffer;

import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProviderHelper;

public final class WildcardPatternProviderHook {

    private WildcardPatternProviderHook() {}

    public static void addCraftingOption(ICraftingProviderHelper craftingTracker, ICraftingMedium medium,
        ICraftingPatternDetails source) {
        if (source == null) {
            return;
        }

        if (!(medium instanceof GTN_WildcardPatternBuffer buffer)) {
            if (source.isCraftable() || !WildcardPatternExpander.containsWildcard(source)) {
                craftingTracker.addCraftingOption(medium, source);
            }

            return;
        }

        if (!buffer.isPrimaryPattern(source)) {
            return;
        }

        for (ICraftingPatternDetails published : buffer.getExpandedPatterns(source)) {
            craftingTracker.addCraftingOption(medium, published);
        }
    }
}
