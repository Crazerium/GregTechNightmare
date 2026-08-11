package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.util.Collections;
import java.util.List;

import com.cleanroommc.modularui.utils.item.IItemHandler;

import appeng.api.networking.crafting.ICraftingPatternDetails;

public final class WildcardPatternExpansionCache {

    private WildcardPatternBlacklist blacklistSnapshot;
    private String sourceFingerprint;
    private List<WildcardPatternVariant> variants = Collections.emptyList();
    private ICraftingPatternDetails boundSource;
    private List<ICraftingPatternDetails> boundPatterns = Collections.emptyList();
    private boolean passthrough;
    private boolean expansionDirty = true;

    public synchronized WildcardPatternBlacklist getBlacklistSnapshot(WildcardBlacklistMode mode,
        IItemHandler inventory) {
        if (blacklistSnapshot == null) {
            blacklistSnapshot = WildcardPatternBlacklist.create(mode, inventory);
        }
        return blacklistSnapshot;
    }

    public synchronized List<ICraftingPatternDetails> getExpandedPatterns(ICraftingPatternDetails source,
        WildcardBlacklistMode mode, IItemHandler inventory) {
        if (source == null) {
            return Collections.emptyList();
        }

        String fingerprint = WildcardPatternExpander.fingerprintSourcePattern(source);
        if (expansionDirty || !fingerprint.equals(sourceFingerprint)) {
            passthrough = source.isCraftable() || !WildcardPatternExpander.containsWildcard(source);
            variants = passthrough ? Collections.emptyList()
                : WildcardPatternExpander.resolveVariants(source, getBlacklistSnapshot(mode, inventory));
            sourceFingerprint = fingerprint;
            expansionDirty = false;
        }

        if (boundSource != source) {
            boundSource = source;
            boundPatterns = passthrough ? Collections.singletonList(source)
                : WildcardPatternExpander.bind(source, variants);
        }
        return boundPatterns;
    }

    public synchronized void invalidatePattern() {
        expansionDirty = true;
        sourceFingerprint = null;
        variants = Collections.emptyList();
        boundSource = null;
        boundPatterns = Collections.emptyList();
    }

    public synchronized void invalidateBlacklist() {
        blacklistSnapshot = null;
        invalidatePattern();
    }
}
