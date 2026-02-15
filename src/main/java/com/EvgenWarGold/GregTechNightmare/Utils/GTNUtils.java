package com.EvgenWarGold.GregTechNightmare.Utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GTNUtils {

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
}
