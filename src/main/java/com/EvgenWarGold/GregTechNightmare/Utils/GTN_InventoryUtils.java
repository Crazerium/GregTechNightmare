package com.EvgenWarGold.GregTechNightmare.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.util.GTUtility;

public class GTN_InventoryUtils {

    // region Main Methods
    public static boolean removeItems(List<ItemStack> inputItems, List<ItemStack> removeItems, boolean simulate) {
        if (inputItems == null || removeItems == null) return false;

        for (ItemStack removeItem : removeItems) {
            int remainingToRemove = removeItem.stackSize;

            if (simulate) {
                int totalCount = 0;
                for (ItemStack currentItem : inputItems) {
                    if (currentItem != null && currentItem.isItemEqual(removeItem)) {
                        totalCount += currentItem.stackSize;
                    }
                }
                if (totalCount < remainingToRemove) {
                    return false;
                }
            } else {
                ListIterator<ItemStack> iterator = inputItems.listIterator();
                while (iterator.hasNext() && remainingToRemove > 0) {
                    ItemStack currentItem = iterator.next();
                    if (currentItem != null && currentItem.isItemEqual(removeItem)) {
                        int currentAmount = currentItem.stackSize;
                        int amountToRemoveFromThis = Math.min(currentAmount, remainingToRemove);
                        currentItem.stackSize -= amountToRemoveFromThis;
                        remainingToRemove -= amountToRemoveFromThis;
                        if (currentItem.stackSize <= 0) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean removeItems(List<ItemStack> inputItems, List<ItemStack> removeItems) {
        return removeItems(inputItems, removeItems, false);
    }

    public static List<ItemStack> copyItems(List<ItemStack> items) {
        List<ItemStack> copy = new ArrayList<>();

        for (ItemStack item : items) {
            if (item != null) {
                copy.add(item.copy());
            }
        }

        return copy;
    }

    public static ItemStack setStackSize(ItemStack itemStack, int amount) {
        return GTUtility.copyAmountUnsafe(amount, itemStack);
    }

    public static ItemStack createItemWithMeta(Item item, int meta, int amount) {
        return setStackSize(new ItemStack(item, 1, meta), amount);
    }

    public static ItemStack createItemWithMeta(Item item, int meta) {
        return createItemWithMeta(item, meta, 1);
    }

    public static ItemStack createItem(Item item, int amount) {
        return createItemWithMeta(item, 0, amount);
    }

    public static ItemStack createItem(Item item) {
        return createItemWithMeta(item, 0, 1);
    }

    public static ItemStack createItemWithMeta(Block block, int meta, int amount) {
        return setStackSize(new ItemStack(block, 1, meta), amount);
    }

    public static ItemStack createItemWithMeta(Block block, int meta) {
        return createItemWithMeta(block, meta, 1);
    }

    public static ItemStack createItem(Block block, int amount) {
        return createItemWithMeta(block, 0, amount);
    }

    public static ItemStack createItem(Block block) {
        return createItemWithMeta(block, 0, 1);
    }

    public static ItemStack[] itemListToArray(List<ItemStack> list) {
        return list.toArray(new ItemStack[0]);
    }

    public static FluidStack[] fluidListToArray(List<FluidStack> list) {
        return list.toArray(new FluidStack[0]);
    }

    public static List<ItemStack> createItemsWithLong(ItemStack itemStack, long amount) {
        if (amount <= 0 || itemStack == null) {
            return new ArrayList<>();
        }
        int listSize = (int) (amount / Integer.MAX_VALUE);
        if (amount % Integer.MAX_VALUE != 0) {
            listSize++;
        }
        List<ItemStack> result = new ArrayList<>(listSize);
        long remainingAmount = amount;
        for (int i = 0; i < listSize; i++) {
            if (remainingAmount > Integer.MAX_VALUE) {
                result.add(GTUtility.copyAmountUnsafe(Integer.MAX_VALUE, itemStack));
                remainingAmount -= Integer.MAX_VALUE;
            } else {
                result.add(GTUtility.copyAmountUnsafe((int) remainingAmount, itemStack));
            }
        }
        return result;
    }

    public static List<ItemStack> createItemsWithLong(Item item, long amount) {
        if (item == null) return new ArrayList<>();

        return createItemsWithLong(createItem(item), amount);
    }

    public static List<ItemStack> createItemsWithLong(Block block, long amount) {
        if (block == null) return new ArrayList<>();

        return createItemsWithLong(createItem(block), amount);
    }

    public static boolean removeFluids(List<FluidStack> inputFluids, List<FluidStack> removeFluids, boolean simulate) {
        if (inputFluids == null || removeFluids == null) return false;
        for (FluidStack removeFluid : removeFluids) {
            int remainingToRemove = removeFluid.amount;
            if (simulate) {
                int totalCount = 0;
                for (FluidStack currentFluid : inputFluids) {
                    if (currentFluid != null && currentFluid.isFluidEqual(removeFluid)) {
                        totalCount += currentFluid.amount;
                    }
                }
                if (totalCount < remainingToRemove) {
                    return false;
                }
            } else {
                ListIterator<FluidStack> iterator = inputFluids.listIterator();
                while (iterator.hasNext() && remainingToRemove > 0) {
                    FluidStack currentFluid = iterator.next();
                    if (currentFluid != null && currentFluid.isFluidEqual(removeFluid)) {
                        int currentAmount = currentFluid.amount;
                        int amountToRemoveFromThis = Math.min(currentAmount, remainingToRemove);
                        currentFluid.amount -= amountToRemoveFromThis;
                        remainingToRemove -= amountToRemoveFromThis;
                        if (currentFluid.amount <= 0) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean removeFluids(List<FluidStack> inputFluids, List<FluidStack> removeFluids) {
        return removeFluids(inputFluids, removeFluids, false);
    }
    // endregion

    // region Help

    // endregion
}
