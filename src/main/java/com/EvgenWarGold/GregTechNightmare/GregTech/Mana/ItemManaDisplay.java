package com.EvgenWarGold.GregTechNightmare.GregTech.Mana;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Display-only item that stands for mana in NEI tooltips of the ME terminal ({@link AEManaStack#getItemStackForNEI}).
 * It has no recipe and is hidden from the creative tabs and NEI.
 */
public class ItemManaDisplay extends Item {

    public ItemManaDisplay() {
        setUnlocalizedName("gtn.ManaDisplay");
        setTextureName("botania:manaPearl");
        setMaxStackSize(64);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return StatCollector.translateToLocal("gtn.mana");
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("rawtypes")
    public void getSubItems(Item item, CreativeTabs tab, List list) {}
}
