package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_WildcardPatternBuffer;

import appeng.api.networking.crafting.ICraftingPatternDetails;

public final class WildcardPatternPushHook {

    private WildcardPatternPushHook() {}

    public static ICraftingPatternDetails unwrap(Object host, ICraftingPatternDetails details) {
        if (!(host instanceof GTN_WildcardPatternBuffer)) {
            return details;
        }

        return details instanceof WildcardPatternDetails ? ((WildcardPatternDetails) details).getDelegate() : details;
    }

    public static boolean canPush(Object host, ICraftingPatternDetails details) {
        if (!(details instanceof WildcardPatternDetails wildcard)) {
            return true;
        }

        if (!(host instanceof GTN_WildcardPatternBuffer buffer)) {
            return false;
        }

        if (wildcard.isBlockedBy(buffer.createBlacklistSnapshot())) {
            return false;
        }

        return WildcardPatternRuntime.preparePush(host, wildcard);
    }
}
