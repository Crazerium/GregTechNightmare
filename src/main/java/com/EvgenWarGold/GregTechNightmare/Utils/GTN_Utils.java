package com.EvgenWarGold.GregTechNightmare.Utils;

import java.util.List;
import java.util.ListIterator;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipe;

public class GTN_Utils {

    /**
     * Create a new {@link ItemStack} of given Item with meta.
     *
     * @param item the Item
     * @param meta the meta value
     * @return a new ItemStack of given Item with meta
     */
    public static ItemStack newItemWithMeta(Item item, int meta) {
        return new ItemStack(item, 1, meta);
    }

    /**
     * Create a new {@link ItemStack} of given Block with meta.
     *
     * @param block the Block
     * @param meta  the meta value
     * @return a new ItemStack of given Item with meta
     */
    public static ItemStack newItemWithMeta(Block block, int meta) {
        return new ItemStack(block, 1, meta);
    }

    /**
     * Localize by the key.
     * If the key does not exist in both of the currently used language and fallback language (English), the key itself
     * is returned.
     *
     * @param key the localization key
     * @return the localized text by the key, or key if the key does not exist.
     * @see StatCollector#translateToLocal(String)
     */
    public static String tr(String key) {
        return StatCollector.translateToLocal(key);
    }

    public static String tr(String key, Object... formatted) {
        return StatCollector.translateToLocalFormatted(key, formatted);
    }

    /**
     * Set the stacksize of the given ItemStack, and return the given stack.
     * The 64 limit is unchecked, be aware.
     *
     * @param itemStack the given stack
     * @param size      the size to set
     * @return the given stack
     */
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

    public static boolean removeItems(List<ItemStack> items, ItemStack itemToRemove, int amount) {
        if (items == null || itemToRemove == null || amount <= 0) {
            return false;
        }

        int remainingToRemove = amount;
        ListIterator<ItemStack> iterator = items.listIterator();

        while (iterator.hasNext() && remainingToRemove > 0) {
            ItemStack currentItem = iterator.next();

            if (currentItem.isItemEqual(itemToRemove)) {
                if (currentItem.stackSize < remainingToRemove) {
                    iterator.remove();
                } else {
                    currentItem.stackSize = currentItem.stackSize - remainingToRemove;
                    remainingToRemove = 0;
                }
            }
        }

        return remainingToRemove == 0;
    }
}
