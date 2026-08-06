package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;

public final class WildcardPatternDetails implements ICraftingPatternDetails {

    private final ICraftingPatternDetails delegate;
    private final IAEStack[] aeInputs;
    private final IAEStack[] aeOutputs;
    private final IAEStack[] condensedAEInputs;
    private final IAEStack[] condensedAEOutputs;
    private final IAEItemStack[] itemInputs;
    private final IAEItemStack[] itemOutputs;
    private final IAEItemStack[] condensedItemInputs;
    private final IAEItemStack[] condensedItemOutputs;
    private int priority;

    public WildcardPatternDetails(ICraftingPatternDetails delegate, IAEStack[] inputs, IAEStack[] outputs) {
        this.delegate = delegate;
        this.aeInputs = AEPatternStackAccess.copy(inputs);
        this.aeOutputs = AEPatternStackAccess.copy(outputs);
        this.condensedAEInputs = condense(aeInputs);
        this.condensedAEOutputs = condense(aeOutputs);
        this.itemInputs = toLegacyItems(aeInputs);
        this.itemOutputs = toLegacyItems(aeOutputs);
        this.condensedItemInputs = toLegacyItems(condensedAEInputs);
        this.condensedItemOutputs = toLegacyItems(condensedAEOutputs);
        this.priority = delegate.getPriority();
    }

    public ICraftingPatternDetails getDelegate() {
        return delegate;
    }

    // These methods exist only in newer AE2/AE2FC APIs, so @Override would break older compile targets.
    public IAEStack[] getAEInputs() {
        return AEPatternStackAccess.copy(aeInputs);
    }

    public IAEStack[] getAEOutputs() {
        return AEPatternStackAccess.copy(aeOutputs);
    }

    public IAEStack[] getCondensedAEInputs() {
        return AEPatternStackAccess.copy(condensedAEInputs);
    }

    public IAEStack[] getCondensedAEOutputs() {
        return AEPatternStackAccess.copy(condensedAEOutputs);
    }

    @Override
    public ItemStack getPattern() {
        return delegate.getPattern();
    }

    @Override
    public boolean isValidItemForSlot(int slotIndex, ItemStack itemStack, World world) {
        if (slotIndex < 0 || slotIndex >= itemInputs.length) return false;

        IAEItemStack expected = itemInputs[slotIndex];
        if (expected == null || itemStack == null) return expected == null && itemStack == null;

        ItemStack expectedStack = expected.getItemStack();
        return expectedStack != null && expectedStack.isItemEqual(itemStack)
            && ItemStack.areItemStackTagsEqual(expectedStack, itemStack);
    }

    @Override
    public boolean isCraftable() {
        return false;
    }

    @Override
    public IAEItemStack[] getInputs() {
        return copyItems(itemInputs);
    }

    @Override
    public IAEItemStack[] getCondensedInputs() {
        return copyItems(condensedItemInputs);
    }

    @Override
    public IAEItemStack[] getCondensedOutputs() {
        return copyItems(condensedItemOutputs);
    }

    @Override
    public IAEItemStack[] getOutputs() {
        return copyItems(itemOutputs);
    }

    @Override
    public boolean canSubstitute() {
        return false;
    }

    @Override
    public ItemStack getOutput(InventoryCrafting craftingInventory, World world) {
        for (IAEItemStack output : itemOutputs) {
            if (output != null && output.getStackSize() > 0) return output.getItemStack();
        }
        return null;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    private static IAEItemStack[] toLegacyItems(IAEStack[] source) {
        List<IAEItemStack> result = new ArrayList<>();
        for (IAEStack stack : source) {
            if (stack instanceof IAEItemStack) {
                result.add(((IAEItemStack) stack).copy());
                continue;
            }

            if (stack instanceof IAEFluidStack) {
                IAEItemStack packet = AE2FCFluidPacketBridge.toPacket((IAEFluidStack) stack);
                if (packet != null) result.add(packet);
            }
        }
        return result.toArray(new IAEItemStack[result.size()]);
    }

    private static IAEItemStack[] copyItems(IAEItemStack[] source) {
        IAEItemStack[] copy = new IAEItemStack[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i] == null ? null : source[i].copy();
        }
        return copy;
    }

    private static IAEStack[] condense(IAEStack[] source) {
        List<IAEStack> condensed = new ArrayList<>();
        for (IAEStack stack : source) {
            if (stack == null || stack.getStackSize() <= 0) continue;

            IAEStack matching = findMatching(condensed, stack);
            if (matching == null) {
                condensed.add(stack.copy());
            } else {
                matching.setStackSize(matching.getStackSize() + stack.getStackSize());
            }
        }
        return condensed.toArray(new IAEStack[condensed.size()]);
    }

    private static IAEStack findMatching(List<IAEStack> candidates, IAEStack wanted) {
        for (IAEStack candidate : candidates) {
            if (sameTypeAndContent(candidate, wanted)) return candidate;
        }
        return null;
    }

    private static boolean sameTypeAndContent(IAEStack first, IAEStack second) {
        if (first instanceof IAEItemStack && second instanceof IAEItemStack) {
            ItemStack firstItem = ((IAEItemStack) first).getItemStack();
            ItemStack secondItem = ((IAEItemStack) second).getItemStack();
            return firstItem != null && secondItem != null && firstItem.isItemEqual(secondItem)
                && ItemStack.areItemStackTagsEqual(firstItem, secondItem);
        }

        if (first instanceof IAEFluidStack && second instanceof IAEFluidStack) {
            FluidStack firstFluid = ((IAEFluidStack) first).getFluidStack();
            FluidStack secondFluid = ((IAEFluidStack) second).getFluidStack();
            return firstFluid != null && secondFluid != null && firstFluid.isFluidEqual(secondFluid);
        }
        return false;
    }
}
