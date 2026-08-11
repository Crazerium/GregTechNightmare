package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import gregtech.api.enums.Materials;

public final class WildcardPatternVariant {

    private final Materials material;
    private final IAEStack<?>[] aeInputs;
    private final IAEStack<?>[] aeOutputs;
    private final IAEStack<?>[] condensedAEInputs;
    private final IAEStack<?>[] condensedAEOutputs;
    private final IAEItemStack[] itemInputs;
    private final IAEItemStack[] itemOutputs;
    private final IAEItemStack[] condensedItemInputs;
    private final IAEItemStack[] condensedItemOutputs;

    public WildcardPatternVariant(Materials material, IAEStack<?>[] inputs, IAEStack<?>[] outputs) {
        this.material = material;
        this.aeInputs = AEPatternStackAccess.copy(inputs);
        this.aeOutputs = AEPatternStackAccess.copy(outputs);
        this.condensedAEInputs = condense(aeInputs);
        this.condensedAEOutputs = condense(aeOutputs);
        this.itemInputs = legacyItems(aeInputs);
        this.itemOutputs = legacyItems(aeOutputs);
        this.condensedItemInputs = legacyItems(condensedAEInputs);
        this.condensedItemOutputs = legacyItems(condensedAEOutputs);
    }

    public WildcardPatternDetails bind(ICraftingPatternDetails delegate) {
        return new WildcardPatternDetails(delegate, this);
    }

    public Materials getMaterial() {
        return material;
    }

    public IAEStack<?>[] getAEInputs() {
        return AEPatternStackAccess.copy(aeInputs);
    }

    public IAEStack<?>[] getAEOutputs() {
        return AEPatternStackAccess.copy(aeOutputs);
    }

    boolean isBlockedBy(WildcardPatternBlacklist blacklist) {
        return blacklist != null && (blacklist.blocksMaterial(material) || blacklist.blocksOutputs(aeOutputs));
    }

    public IAEStack<?>[] getCondensedAEInputs() {
        return AEPatternStackAccess.copy(condensedAEInputs);
    }

    public IAEStack<?>[] getCondensedAEInputsView() {
        return condensedAEInputs;
    }

    public IAEStack<?>[] getCondensedAEOutputs() {
        return AEPatternStackAccess.copy(condensedAEOutputs);
    }

    public IAEStack<?>[] getCondensedAEOutputsView() {
        return condensedAEOutputs;
    }

    public IAEItemStack[] getItemInputs() {
        return copyItems(itemInputs);
    }

    public IAEItemStack getItemInput(int index) {
        return index < 0 || index >= itemInputs.length ? null : itemInputs[index];
    }

    public int getItemInputCount() {
        return itemInputs.length;
    }

    public IAEItemStack[] getItemOutputs() {
        return copyItems(itemOutputs);
    }

    public ItemStack getFirstItemOutput() {
        for (IAEItemStack output : itemOutputs) {
            if (output != null && output.getStackSize() > 0) return output.getItemStack();
        }
        return null;
    }

    public IAEItemStack[] getCondensedItemInputs() {
        return copyItems(condensedItemInputs);
    }

    public IAEItemStack[] getCondensedItemOutputs() {
        return copyItems(condensedItemOutputs);
    }

    private static IAEItemStack[] legacyItems(IAEStack<?>[] source) {
        List<IAEItemStack> result = new ArrayList<>();
        for (IAEStack<?> stack : source) {
            if (stack instanceof IAEItemStack aeItemStack) {
                result.add(aeItemStack.copy());
            } else if (stack instanceof IAEFluidStack aeFluidStack) {
                IAEItemStack packet = AE2FCFluidPacketBridge.toPacket(aeFluidStack);
                if (packet != null) {
                    result.add(packet);
                }
            }
        }
        return result.toArray(new IAEItemStack[0]);
    }

    private static IAEItemStack[] copyItems(IAEItemStack[] source) {
        IAEItemStack[] copy = new IAEItemStack[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i] == null ? null : source[i].copy();
        }
        return copy;
    }

    private static IAEStack<?>[] condense(IAEStack<?>[] source) {
        List<IAEStack<?>> condensed = new ArrayList<>();
        for (IAEStack<?> stack : source) {
            if (stack == null || stack.getStackSize() <= 0) {
                continue;
            }
            IAEStack<?> matching = findMatching(condensed, stack);
            if (matching == null) {
                condensed.add(stack.copy());
            } else {
                matching.setStackSize(matching.getStackSize() + stack.getStackSize());
            }
        }
        return condensed.toArray(new IAEStack[0]);
    }

    private static IAEStack<?> findMatching(List<IAEStack<?>> candidates, IAEStack<?> wanted) {
        for (IAEStack<?> candidate : candidates) {
            if (sameTypeAndContent(candidate, wanted)) {
                return candidate;
            }
        }
        return null;
    }

    private static boolean sameTypeAndContent(IAEStack<?> first, IAEStack<?> second) {
        if (first instanceof IAEItemStack firstStack && second instanceof IAEItemStack secondStack) {
            ItemStack a = firstStack.getItemStack();
            ItemStack b = secondStack.getItemStack();
            return a != null && b != null && a.isItemEqual(b) && ItemStack.areItemStackTagsEqual(a, b);
        }
        if (first instanceof IAEFluidStack firstFluidStack && second instanceof IAEFluidStack secondFluidStack) {
            FluidStack a = firstFluidStack.getFluidStack();
            FluidStack b = secondFluidStack.getFluidStack();
            return a != null && b != null && a.isFluidEqual(b);
        }
        return false;
    }
}
