package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.util.Collections;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_WildcardPatternBuffer;

import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProviderHelper;

public final class WildcardPatternProviderHook {

    private WildcardPatternProviderHook() {}

    public static void addCraftingOption(ICraftingProviderHelper craftingTracker, ICraftingMedium medium,
        ICraftingPatternDetails source) {
        if (source == null) return;

        if (!(medium instanceof GTN_WildcardPatternBuffer)) {
            if (source.isCraftable() || !WildcardPatternExpander.containsWildcard(source)) {
                craftingTracker.addCraftingOption(medium, source);
            }
            return;
        }

        GTN_WildcardPatternBuffer buffer = (GTN_WildcardPatternBuffer) medium;
        if (!buffer.isPrimaryPattern(source)) return;

        if (source.isCraftable() || !WildcardPatternExpander.containsWildcard(source)) {
            craftingTracker.addCraftingOption(medium, source);
            return;
        }

        for (ICraftingPatternDetails resolved : WildcardPatternExpander.expandAll(Collections.singletonList(source))) {
            craftingTracker.addCraftingOption(medium, resolved);
        }
    }
}
