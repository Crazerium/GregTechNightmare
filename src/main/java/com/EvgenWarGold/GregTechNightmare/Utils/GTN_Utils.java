package com.EvgenWarGold.GregTechNightmare.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;

public class GTN_Utils {

    public static ItemStack newItemWithMeta(Item item, int meta) {
        return new ItemStack(item, 1, meta);
    }

    public static ItemStack newItemWithMeta(Block block, int meta) {
        return new ItemStack(block, 1, meta);
    }

    public static String tr(String key) {
        return StatCollector.translateToLocal(key);
    }

    public static String tr(String key, Object... formatted) {
        return StatCollector.translateToLocalFormatted(key, formatted);
    }

    public static ItemStack setStackSize(@NotNull ItemStack itemStack, int size) {
        itemStack.stackSize = size;
        return itemStack;
    }

    public static GTRecipe findRecipe(List<ItemStack> inputList, RecipeMap<?> recipeMap) {
        ItemStack[] inputArr = inputList.toArray(new ItemStack[0]);
        return recipeMap.findRecipeQuery()
            .items(inputArr)
            .find();
    }

    public static GTRecipe findRecipe(ItemStack[] itemStacks, RecipeMap<?> recipeMap) {
        return recipeMap.findRecipeQuery()
            .items(itemStacks)
            .find();
    }

    public static boolean removeItems(List<ItemStack> items, ItemStack itemToRemove, int amount, boolean simulate) {
        if (items == null || itemToRemove == null || amount <= 0) {
            return false;
        }

        int totalCount = 0;
        for (ItemStack item : items) {
            if (item != null && item.isItemEqual(itemToRemove)) {
                totalCount += item.stackSize;
            }
        }

        if (totalCount < amount) {
            return false;
        }

        if (simulate) {
            return true;
        }

        int remainingToRemove = amount;
        ListIterator<ItemStack> iterator = items.listIterator();

        while (iterator.hasNext() && remainingToRemove > 0) {
            ItemStack currentItem = iterator.next();

            if (currentItem != null && currentItem.isItemEqual(itemToRemove)) {
                int currentAmount = currentItem.stackSize;

                if (currentAmount <= remainingToRemove) {
                    currentItem.stackSize = currentAmount - remainingToRemove;
                    remainingToRemove -= currentAmount;
                    iterator.remove();
                } else {
                    currentItem.stackSize = currentAmount - remainingToRemove;
                    remainingToRemove = 0;
                }
            }
        }

        return true;
    }

    public static boolean removeItems(List<ItemStack> items, ItemStack itemToRemove, int amount) {
        return removeItems(items, itemToRemove, amount, false);
    }

    public static List<ItemStack> addItemsToList(ItemStack item, long amount) {
        List<ItemStack> result = new ArrayList<>();

        if (amount <= 0 || item == null) {
            return result;
        }

        long remainingAmount = amount;

        while (remainingAmount > Integer.MAX_VALUE) {
            result.add(GTUtility.copyAmountUnsafe(Integer.MAX_VALUE, item));
            remainingAmount -= Integer.MAX_VALUE;
        }

        if (remainingAmount > 0) {
            result.add(GTUtility.copyAmountUnsafe((int) remainingAmount, item));
        }

        return result;
    }

    public static ItemStack[] addItemsToArrays(ItemStack item, long amount) {
        if (amount <= 0 || item == null) {
            return new ItemStack[0];
        }

        int arraySize = (int) (amount / Integer.MAX_VALUE);
        if (amount % Integer.MAX_VALUE != 0) {
            arraySize++;
        }

        ItemStack[] result = new ItemStack[arraySize];
        long remainingAmount = amount;

        for (int i = 0; i < result.length; i++) {
            if (remainingAmount > Integer.MAX_VALUE) {
                result[i] = GTUtility.copyAmountUnsafe(Integer.MAX_VALUE, item);
                remainingAmount -= Integer.MAX_VALUE;
            } else {
                result[i] = GTUtility.copyAmountUnsafe((int) remainingAmount, item);
            }
        }

        return result;
    }
}
