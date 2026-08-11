package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import gregtech.api.enums.Materials;

public final class WildcardPatternDetails implements ICraftingPatternDetails {

    private final ICraftingPatternDetails delegate;
    private final WildcardPatternVariant variant;
    private int priority;

    public WildcardPatternDetails(ICraftingPatternDetails delegate, WildcardPatternVariant variant) {
        this.delegate = delegate;
        this.variant = variant;
        this.priority = delegate.getPriority();
    }

    public ICraftingPatternDetails getDelegate() {
        return delegate;
    }

    public Materials getMaterial() {
        return variant.getMaterial();
    }

    public boolean isBlockedBy(WildcardPatternBlacklist blacklist) {
        return variant.isBlockedBy(blacklist);
    }

    public IAEStack<?>[] getAEInputs() {
        return variant.getAEInputs();
    }

    public IAEStack<?>[] getAEOutputs() {
        return variant.getAEOutputs();
    }

    public IAEStack<?>[] getCondensedAEInputs() {
        return variant.getCondensedAEInputs();
    }

    public IAEStack<?>[] getCondensedAEOutputs() {
        return variant.getCondensedAEOutputs();
    }

    IAEStack<?>[] getCondensedAEInputsView() {
        return variant.getCondensedAEInputsView();
    }

    IAEStack<?>[] getCondensedAEOutputsView() {
        return variant.getCondensedAEOutputsView();
    }

    @Override
    public ItemStack getPattern() {
        return delegate.getPattern();
    }

    @Override
    public boolean isValidItemForSlot(int slotIndex, ItemStack itemStack, World world) {
        if (slotIndex < 0 || slotIndex >= variant.getItemInputCount()) {
            return false;
        }

        IAEItemStack expected = variant.getItemInput(slotIndex);
        if (expected == null || itemStack == null) {
            return expected == null && itemStack == null;
        }

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
        return variant.getItemInputs();
    }

    @Override
    public IAEItemStack[] getCondensedInputs() {
        return variant.getCondensedItemInputs();
    }

    @Override
    public IAEItemStack[] getCondensedOutputs() {
        return variant.getCondensedItemOutputs();
    }

    @Override
    public IAEItemStack[] getOutputs() {
        return variant.getItemOutputs();
    }

    @Override
    public boolean canSubstitute() {
        return false;
    }

    @Override
    public ItemStack getOutput(InventoryCrafting craftingInv, World world) {
        return variant.getFirstItemOutput();
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
