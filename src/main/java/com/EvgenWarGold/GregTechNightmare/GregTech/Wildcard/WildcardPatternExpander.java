package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

public final class WildcardPatternExpander {

    private WildcardPatternExpander() {}

    public static boolean containsWildcard(ICraftingPatternDetails details) {
        return details != null && (containsWildcard(AEPatternStackAccess.getInputs(details))
            || containsWildcard(AEPatternStackAccess.getOutputs(details)));
    }

    public static List<ICraftingPatternDetails> expandAll(Collection<ICraftingPatternDetails> sourcePatterns) {
        List<ICraftingPatternDetails> result = new ArrayList<>();
        if (sourcePatterns == null || sourcePatterns.isEmpty()) return result;

        Set<String> fingerprints = new HashSet<>();
        for (ICraftingPatternDetails source : sourcePatterns) {
            if (source == null) continue;

            if (source.isCraftable() || !containsWildcard(source)) {
                addUnique(result, fingerprints, source);
                continue;
            }

            IAEStack[] sourceInputs = AEPatternStackAccess.getInputs(source);
            IAEStack[] sourceOutputs = AEPatternStackAccess.getOutputs(source);
            for (Materials material : Materials.values()) {
                if (material == null) continue;

                IAEStack[] resolvedInputs = resolve(sourceInputs, material);
                if (resolvedInputs == null) continue;

                IAEStack[] resolvedOutputs = resolve(sourceOutputs, material);
                if (resolvedOutputs == null) continue;

                addUnique(
                    result,
                    fingerprints,
                    new WildcardPatternDetails(source, resolvedInputs, resolvedOutputs));
            }
        }
        return result;
    }

    private static boolean containsWildcard(IAEStack[] stacks) {
        for (IAEStack stack : stacks) {
            if (stack instanceof IAEItemStack && isWildcard(((IAEItemStack) stack).getItemStack())) return true;
        }
        return false;
    }

    private static IAEStack[] resolve(IAEStack[] source, Materials material) {
        IAEStack[] result = new IAEStack[source.length];
        for (int i = 0; i < source.length; i++) {
            IAEStack stack = source[i];
            if (stack == null) continue;

            if (!(stack instanceof IAEItemStack)) {
                result[i] = stack.copy();
                continue;
            }

            ItemStack itemStack = ((IAEItemStack) stack).getItemStack();
            if (!isWildcard(itemStack)) {
                result[i] = stack.copy();
                continue;
            }

            WildcardPrefix wildcard = WildcardPrefix.byMeta(itemStack.getItemDamage());
            if (wildcard == null) return null;

            IAEStack resolved = wildcard.isFluid() ? resolveFluid(wildcard, material, stack.getStackSize())
                : resolveItem(wildcard, material, stack.getStackSize());
            if (resolved == null) return null;
            result[i] = resolved;
        }
        return result;
    }

    private static IAEFluidStack resolveFluid(WildcardPrefix wildcard, Materials material, long amount) {
        FluidStack fluid = WildcardFluidResolver.resolve(material, wildcard.getFluidMode(), amount);
        if (fluid == null || fluid.getFluid() == null || fluid.amount <= 0) return null;

        IAEFluidStack resolved = AEApi.instance().storage().createFluidStack(fluid);
        if (resolved != null) resolved.setStackSize(amount);
        return resolved;
    }

    private static IAEItemStack resolveItem(WildcardPrefix wildcard, Materials material, long amount) {
        OrePrefixes orePrefix = wildcard.getOrePrefix();
        if (orePrefix == null) return null;

        ItemStack item = GTOreDictUnificator.get(orePrefix, material, 1L);
        if (GTUtility.isStackInvalid(item)) return null;

        IAEItemStack resolved = AEApi.instance().storage().createItemStack(item);
        if (resolved != null) resolved.setStackSize(amount);
        return resolved;
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

    private static void appendStacks(StringBuilder builder, IAEStack[] stacks) {
        for (IAEStack stack : stacks) {
            if (stack instanceof IAEItemStack) {
                appendItemStack(builder, (IAEItemStack) stack);
            } else if (stack instanceof IAEFluidStack) {
                appendFluidStack(builder, (IAEFluidStack) stack);
            }
        }
    }

    private static void appendItemStack(StringBuilder builder, IAEItemStack stack) {
        ItemStack item = stack.getItemStack();
        if (item == null) return;

        builder.append('I')
            .append(Item.getIdFromItem(item.getItem()))
            .append(':')
            .append(item.getItemDamage())
            .append(':')
            .append(stack.getStackSize())
            .append(':')
            .append(item.hasTagCompound() ? item.getTagCompound().toString() : "")
            .append(';');
    }

    private static void appendFluidStack(StringBuilder builder, IAEFluidStack stack) {
        FluidStack fluid = stack.getFluidStack();
        if (fluid == null || fluid.getFluid() == null) return;

        builder.append('F')
            .append(fluid.getFluid().getName())
            .append(':')
            .append(stack.getStackSize())
            .append(':')
            .append(fluid.tag == null ? "" : fluid.tag.toString())
            .append(';');
    }
}
