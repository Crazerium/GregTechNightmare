package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_WildcardPatternBuffer;

import appeng.api.networking.crafting.ICraftingPatternDetails;

/** Maps synthetic wildcard recipes back to the encoded pattern held by GT5U. */
public final class WildcardPatternPushHook {

    private WildcardPatternPushHook() {}

    /**
     * Converts a synthetic wildcard variant back to the encoded pattern stored by GT5U.
     *
     * @param host    crafting medium receiving the request
     * @param details pattern selected by AE
     * @return original encoded pattern for the wildcard buffer, otherwise the supplied pattern
     * @author Crazerium
     * @reason GT5U resolves live pattern slots using the original encoded pattern identity
     */
    public static ICraftingPatternDetails unwrap(Object host, ICraftingPatternDetails details) {
        if (!(host instanceof GTN_WildcardPatternBuffer)) return details;
        return details instanceof WildcardPatternDetails ? ((WildcardPatternDetails) details).getDelegate() : details;
    }

    /**
     * Revalidates a wildcard request and reserves its live GT5U pattern slot.
     *
     * @param host    crafting medium receiving the request
     * @param details pattern selected by AE
     * @return {@code true} when the request is still valid and safe to execute
     * @author Crazerium
     * @reason AE can retain crafting plans after the source pattern or blacklist has changed
     */
    public static boolean canPush(Object host, ICraftingPatternDetails details) {
        if (!(details instanceof WildcardPatternDetails)) return true;
        if (!(host instanceof GTN_WildcardPatternBuffer)) return false;
        GTN_WildcardPatternBuffer buffer = (GTN_WildcardPatternBuffer) host;
        WildcardPatternDetails wildcard = (WildcardPatternDetails) details;
        if (wildcard.isBlockedBy(buffer.createBlacklistSnapshot())) return false;
        return WildcardPatternRuntime.preparePush(host, wildcard);
    }
}
