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

    /**
     * Returns material-resolved patterns while reusing the expensive expansion across AE grid rebuilds.
     *
     * @param source    encoded AE processing pattern
     * @param mode      active blacklist mode
     * @param inventory blacklist filter inventory
     * @return cached concrete patterns bound to the current source object
     * @author Crazerium
     * @reason Repeating ore-dictionary and material resolution for every AE cache rebuild causes TPS spikes
     */
    public synchronized List<ICraftingPatternDetails> getExpandedPatterns(ICraftingPatternDetails source,
        WildcardBlacklistMode mode, IItemHandler inventory) {
        if (source == null) return Collections.emptyList();

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

    /**
     * Invalidates only data derived from the encoded pattern.
     *
     * @author Crazerium
     * @reason A changed pattern requires new variants but can keep the existing blacklist snapshot
     */
    public synchronized void invalidatePattern() {
        expansionDirty = true;
        sourceFingerprint = null;
        variants = Collections.emptyList();
        boundSource = null;
        boundPatterns = Collections.emptyList();
    }

    /**
     * Invalidates both the blacklist snapshot and every variant filtered through it.
     *
     * @author Crazerium
     * @reason Blacklist edits can change which material expansions are published to AE
     */
    public synchronized void invalidateBlacklist() {
        blacklistSnapshot = null;
        invalidatePattern();
    }
}
