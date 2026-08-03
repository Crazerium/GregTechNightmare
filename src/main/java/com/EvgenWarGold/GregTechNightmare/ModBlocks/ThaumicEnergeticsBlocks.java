package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;

import gregtech.api.enums.Mods;

public enum ThaumicEnergeticsBlocks {

    // spotless:off
    ArcaneAssembler("thaumicenergistics.block.arcane.assembler"),
    ;
    //spotless:on

    private final String unlocalizedName;
    private final String localizedName;
    private final int meta;
    private ModBlock cachedBlock;

    ThaumicEnergeticsBlocks(String unlocalizedName, String localizedName, int meta) {
        this.unlocalizedName = unlocalizedName;
        this.localizedName = localizedName;
        this.meta = meta;
    }

    ThaumicEnergeticsBlocks(String unlocalizedName, String localizedName) {
        this(unlocalizedName, localizedName, 0);
    }

    ThaumicEnergeticsBlocks(String unlocalizedName, int meta) {
        this(unlocalizedName, unlocalizedName, meta);
    }

    ThaumicEnergeticsBlocks(String unlocalizedName) {
        this(unlocalizedName, unlocalizedName, 0);
    }

    private ModBlock getBlock() {
        if (cachedBlock == null) {
            cachedBlock = new ModBlock(Mods.ThaumicEnergistics, unlocalizedName, localizedName, meta);
        }
        return cachedBlock;
    }

    public Block get() {
        return getBlock().get();
    }

    public int getMeta() {
        return meta;
    }

    public ItemStack getItemStack(int count) {
        return getBlock().getItemStack(count);
    }

    public ItemStack getItemStack() {
        return getItemStack(1);
    }
}
