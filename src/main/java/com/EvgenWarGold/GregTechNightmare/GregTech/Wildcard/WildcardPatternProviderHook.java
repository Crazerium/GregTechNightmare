package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_WildcardPatternBuffer;

import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProviderHelper;

/** Expands wildcard patterns while leaving AE cache invalidation under GT5U control. */
public final class WildcardPatternProviderHook {

    private WildcardPatternProviderHook() {}

    /**
     * Publishes normal patterns unchanged and expands the primary wildcard pattern of the dedicated buffer.
     *
     * @param craftingTracker AE crafting registry receiving patterns
     * @param medium          crafting medium that owns the source pattern
     * @param source          source pattern being published by GT5U
     * @author Crazerium
     * @reason Wildcard buffers advertise concrete recipes while ordinary GT5U providers retain native behavior
     */
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

        for (ICraftingPatternDetails published : buffer.getExpandedPatterns(source)) {
            craftingTracker.addCraftingOption(medium, published);
        }
    }
}
