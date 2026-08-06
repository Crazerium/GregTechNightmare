package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.EvgenWarGold.GregTechNightmare.GregTech.Items.GTN_Items;

import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

/** Expands wildcard processing patterns once per compatible GregTech material. */
public final class WildcardPatternExpander {

    private static final Map<OrePrefixes, Map<Materials, ItemStack>> RESOLVED_ITEMS = new IdentityHashMap<>();

    private WildcardPatternExpander() {}

    public static boolean containsWildcard(ICraftingPatternDetails details) {
        return details != null && (containsWildcard(AEPatternStackAccess.getInputs(details))
            || containsWildcard(AEPatternStackAccess.getOutputs(details)));
    }

    public static List<ICraftingPatternDetails> expandAll(Collection<ICraftingPatternDetails> sourcePatterns) {
        return expandAll(sourcePatterns, null);
    }

    public static List<ICraftingPatternDetails> expandAll(Collection<ICraftingPatternDetails> sourcePatterns,
        WildcardPatternBlacklist blacklist) {
        if (sourcePatterns == null || sourcePatterns.isEmpty()) return Collections.emptyList();

        List<ICraftingPatternDetails> result = new ArrayList<>();
        Set<String> fingerprints = new HashSet<>();

        for (ICraftingPatternDetails source : sourcePatterns) {
            if (source == null) continue;
            if (source.isCraftable() || !containsWildcard(source)) {
                addUnique(result, fingerprints, source);
                continue;
            }

            for (WildcardPatternVariant variant : resolveVariants(source, blacklist)) {
                WildcardPatternDetails details = variant.bind(source);
                addUnique(result, fingerprints, details);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Resolves one processing pattern for every compatible GregTech material.
     *
     * @param source    encoded processing pattern containing wildcard tokens
     * @param blacklist snapshot used to filter materials or concrete outputs
     * @return immutable list of resolved material variants
     * @author Crazerium
     * @reason AE crafting cannot plan recipes that contain phantom wildcard tokens
     */
    static List<WildcardPatternVariant> resolveVariants(ICraftingPatternDetails source,
        WildcardPatternBlacklist blacklist) {
        if (source == null || source.isCraftable()) return Collections.emptyList();

        IAEStack[] sourceInputs = AEPatternStackAccess.getInputs(source);
        IAEStack[] sourceOutputs = AEPatternStackAccess.getOutputs(source);

        List<WildcardPatternVariant> result = new ArrayList<>();
        Set<String> fingerprints = new HashSet<>();
        for (Materials material : Materials.values()) {
            if (material == null || blacklist != null && blacklist.blocksMaterial(material)) continue;

            IAEStack[] resolvedInputs = resolve(sourceInputs, material);
            if (resolvedInputs == null) continue;
            IAEStack[] resolvedOutputs = resolve(sourceOutputs, material);
            if (resolvedOutputs == null || blacklist != null && blacklist.blocksOutputs(resolvedOutputs)) continue;

            String fingerprint = fingerprint(resolvedInputs, resolvedOutputs);
            if (fingerprints.add(fingerprint)) {
                result.add(new WildcardPatternVariant(material, resolvedInputs, resolvedOutputs));
            }
        }
        return Collections.unmodifiableList(result);
    }

    static List<ICraftingPatternDetails> bind(ICraftingPatternDetails source, List<WildcardPatternVariant> variants) {
        if (source == null || variants == null || variants.isEmpty()) return Collections.emptyList();

        List<ICraftingPatternDetails> result = new ArrayList<>(variants.size());
        for (WildcardPatternVariant variant : variants) {
            result.add(variant.bind(source));
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Creates a stable fingerprint for cache invalidation without retaining the source pattern object.
     *
     * @param details source AE pattern
     * @return fingerprint containing the pattern stack, flags, priority, inputs and outputs
     * @author Crazerium
     * @reason AE may recreate pattern-detail objects while the encoded recipe remains unchanged
     */
    public static String fingerprintSourcePattern(ICraftingPatternDetails details) {
        if (details == null) return "null";

        StringBuilder builder = new StringBuilder(256);
        appendPatternStack(builder, details.getPattern());
        builder.append('|')
            .append(details.isCraftable())
            .append('|')
            .append(details.canSubstitute())
            .append('|')
            .append(details.getPriority())
            .append('|');
        appendStacks(builder, AEPatternStackAccess.getInputs(details));
        builder.append(" -> ");
        appendStacks(builder, AEPatternStackAccess.getOutputs(details));
        return builder.toString();
    }

    public static String fingerprintSourcePatterns(Collection<ICraftingPatternDetails> patterns) {
        if (patterns == null || patterns.isEmpty()) return "";
        StringBuilder builder = new StringBuilder(Math.max(256, patterns.size() * 128));
        for (ICraftingPatternDetails details : patterns) {
            builder.append(fingerprintSourcePattern(details))
                .append('\n');
        }
        return builder.toString();
    }

    public static String fingerprintPatternStack(ItemStack pattern) {
        StringBuilder builder = new StringBuilder(96);
        appendPatternStack(builder, pattern);
        return builder.toString();
    }

    private static boolean containsWildcard(IAEStack[] stacks) {
        if (stacks == null) return false;
        for (IAEStack stack : stacks) {
            if (!(stack instanceof IAEItemStack)) continue;
            if (isWildcard(((IAEItemStack) stack).getItemStack())) return true;
        }
        return false;
    }

    private static IAEStack[] resolve(IAEStack[] source, Materials material) {
        if (source == null) return new IAEStack[0];
        IAEStack[] result = new IAEStack[source.length];

        for (int i = 0; i < source.length; i++) {
            IAEStack input = source[i];
            if (input == null) continue;

            if (!(input instanceof IAEItemStack)) {
                result[i] = input.copy();
                continue;
            }

            ItemStack itemStack = ((IAEItemStack) input).getItemStack();
            if (!isWildcard(itemStack)) {
                result[i] = input.copy();
                continue;
            }

            WildcardPrefix wildcard = WildcardPrefix.byMeta(itemStack.getItemDamage());
            if (wildcard == null) return null;

            if (wildcard.isFluid()) {
                FluidStack fluid = WildcardFluidResolver
                    .resolve(material, wildcard.getFluidMode(), input.getStackSize());
                if (fluid == null || fluid.getFluid() == null || fluid.amount <= 0) return null;
                IAEFluidStack aeFluid = AEApi.instance()
                    .storage()
                    .createFluidStack(fluid);
                if (aeFluid == null) return null;
                aeFluid.setStackSize(input.getStackSize());
                result[i] = aeFluid;
                continue;
            }

            OrePrefixes orePrefix = wildcard.getOrePrefix();
            if (orePrefix == null) return null;
            ItemStack resolved = resolveItem(orePrefix, material);
            if (resolved == null) return null;

            IAEItemStack aeResolved = AEApi.instance()
                .storage()
                .createItemStack(resolved);
            if (aeResolved == null) return null;
            aeResolved.setStackSize(input.getStackSize());
            result[i] = aeResolved;
        }
        return result;
    }

    private static ItemStack resolveItem(OrePrefixes prefix, Materials material) {
        synchronized (RESOLVED_ITEMS) {
            Map<Materials, ItemStack> byMaterial = RESOLVED_ITEMS.get(prefix);
            if (byMaterial != null && byMaterial.containsKey(material)) {
                ItemStack cached = byMaterial.get(material);
                return cached == null ? null : cached.copy();
            }
        }

        ItemStack resolved = GTOreDictUnificator.get(prefix, material, 1L);
        ItemStack cached = GTUtility.isStackInvalid(resolved) ? null : GTUtility.copyAmount(1, resolved);

        synchronized (RESOLVED_ITEMS) {
            Map<Materials, ItemStack> byMaterial = RESOLVED_ITEMS.get(prefix);
            if (byMaterial == null) {
                byMaterial = new IdentityHashMap<>();
                RESOLVED_ITEMS.put(prefix, byMaterial);
            }
            byMaterial.put(material, cached);
        }
        return cached == null ? null : cached.copy();
    }

    private static boolean isWildcard(ItemStack stack) {
        return stack != null && stack.getItem() == GTN_Items.WILDCARD_PREFIX;
    }

    private static void addUnique(List<ICraftingPatternDetails> result, Set<String> fingerprints,
        ICraftingPatternDetails details) {
        IAEStack[] inputs = details instanceof WildcardPatternDetails
            ? ((WildcardPatternDetails) details).getCondensedAEInputs()
            : AEPatternStackAccess.getInputs(details);
        IAEStack[] outputs = details instanceof WildcardPatternDetails
            ? ((WildcardPatternDetails) details).getCondensedAEOutputs()
            : AEPatternStackAccess.getOutputs(details);
        if (fingerprints.add(fingerprint(inputs, outputs))) result.add(details);
    }

    private static String fingerprint(IAEStack[] inputs, IAEStack[] outputs) {
        StringBuilder builder = new StringBuilder(256);
        appendStacks(builder, inputs);
        builder.append(" -> ");
        appendStacks(builder, outputs);
        return builder.toString();
    }

    private static void appendPatternStack(StringBuilder builder, ItemStack pattern) {
        if (pattern == null) {
            builder.append("null");
            return;
        }
        builder.append(Item.getIdFromItem(pattern.getItem()))
            .append(':')
            .append(pattern.getItemDamage())
            .append(':')
            .append(pattern.stackSize)
            .append(':')
            .append(
                pattern.hasTagCompound() ? pattern.getTagCompound()
                    .toString() : "");
    }

    private static void appendStacks(StringBuilder builder, IAEStack[] stacks) {
        if (stacks == null) return;
        for (IAEStack stack : stacks) {
            if (stack == null) continue;
            if (stack instanceof IAEItemStack) {
                ItemStack item = ((IAEItemStack) stack).getItemStack();
                if (item == null) continue;
                builder.append('I')
                    .append(Item.getIdFromItem(item.getItem()))
                    .append(':')
                    .append(item.getItemDamage())
                    .append(':')
                    .append(stack.getStackSize())
                    .append(':')
                    .append(
                        item.hasTagCompound() ? item.getTagCompound()
                            .toString() : "")
                    .append(';');
            } else if (stack instanceof IAEFluidStack) {
                FluidStack fluid = ((IAEFluidStack) stack).getFluidStack();
                if (fluid == null || fluid.getFluid() == null) continue;
                builder.append('F')
                    .append(
                        fluid.getFluid()
                            .getName())
                    .append(':')
                    .append(stack.getStackSize())
                    .append(':')
                    .append(fluid.tag == null ? "" : fluid.tag.toString())
                    .append(';');
            }
        }
    }
}
