package com.EvgenWarGold.GregTechNightmare.Api;

import static gregtech.api.util.GTUtility.copyAmount;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;

import gregtech.api.enums.Mods;
import gregtech.api.util.GTModHandler;

public class ModBlock {

    public final String unlocalizedName;
    public final String localizedName;
    public final int meta;
    public final String modID;
    protected Block block;
    protected ItemStack itemStack;

    public ModBlock(@NotNull Mods mod, @NotNull String unlocalizedName, int meta, @NotNull String localizedName) {
        this(mod.ID, unlocalizedName, meta, localizedName);
    }

    public ModBlock(@NotNull String modID, @NotNull String unlocalizedName, int meta, @NotNull String localizedName) {
        this.modID = modID;
        this.unlocalizedName = unlocalizedName;
        this.meta = meta;
        this.localizedName = localizedName;
    }

    public Block getBlock() {
        if (block == null) {
            ItemStack stack = GTModHandler.getModItem(modID, unlocalizedName, 1, meta);
            if (stack != null && stack.getItem() != null) {
                block = Block.getBlockFromItem(stack.getItem());
                if (block != null) {
                    itemStack = stack;
                }
            }

            if (block == null) {
                block = createFallbackBlock();
            }
        }
        return block;
    }

    public ItemStack getItemStack(int count) {
        if (itemStack == null) {
            itemStack = GTModHandler.getModItem(modID, unlocalizedName, 1, meta);
            if (itemStack == null || itemStack.getItem() == null) {
                itemStack = createFallbackItemStack(count);
            }
        }
        return copyAmount(count, itemStack);
    }

    protected Block createFallbackBlock() {
        ItemStack fallbackStack = createFallbackItemStack(1);
        if (fallbackStack != null && fallbackStack.getItem() != null) {
            Block fallbackBlock = Block.getBlockFromItem(fallbackStack.getItem());
            if (fallbackBlock != null) {
                itemStack = fallbackStack;
                return fallbackBlock;
            }
        }
        return null;
    }

    protected ItemStack createFallbackItemStack(int count) {
        ItemStack stack = GTN_ItemList.TestItem.get(count);
        String stackName = EnumChatFormatting.WHITE + modID + " : " + localizedName;
        stack.setStackDisplayName(EnumChatFormatting.RESET + stackName);
        return stack;
    }
}
