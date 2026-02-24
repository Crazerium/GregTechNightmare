package com.EvgenWarGold.GregTechNightmare.Api;

import static gregtech.api.util.GTUtility.copyAmount;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.NotNull;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;

import gregtech.api.enums.Mods;
import gregtech.api.util.GTModHandler;

public class ModItem {

    public final String unlocalizedName;
    public final String localizedName;
    public final int meta;
    public final String modID;
    protected ItemStack itemStack;

    public ModItem(@NotNull Mods mod, @NotNull String unlocalizedName, int meta, @NotNull String localizedName) {
        this(mod.ID, unlocalizedName, meta, localizedName);
    }

    public ModItem(@NotNull String modID, @NotNull String unlocalizedName, int meta, @NotNull String localizedName) {
        this.modID = modID;
        this.unlocalizedName = unlocalizedName;
        this.meta = meta;
        this.localizedName = localizedName;
    }

    public ItemStack get(int count) {
        if (itemStack == null) {
            itemStack = GTModHandler.getModItem(modID, unlocalizedName, 1, meta);
            if (itemStack == null) {
                itemStack = createFallbackItem(localizedName, count);
            }
        }
        return copyAmount(count, itemStack);
    }

    public ItemStack createFallbackItem(String name, int count) {
        ItemStack stack = GTN_ItemList.TestItem.get(count);
        String stackName = EnumChatFormatting.WHITE + modID + " : " + name;
        stack.setStackDisplayName(EnumChatFormatting.RESET + stackName);
        return stack;
    }
}
