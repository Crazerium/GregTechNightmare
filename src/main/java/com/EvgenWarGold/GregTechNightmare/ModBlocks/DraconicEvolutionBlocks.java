package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import gregtech.api.enums.Mods;

public enum DraconicEvolutionBlocks {

    // spotless:off
    DissEnchanter("dissEnchanter"),
    ;
    //spotless:on

    private final String unlocalizedName;
    private final String localizedName;
    private final int meta;
    private ModBlock cachedBlock;

    DraconicEvolutionBlocks(String unlocalizedName, String localizedName, int meta) {
        this.unlocalizedName = unlocalizedName;
        this.localizedName = localizedName;
        this.meta = meta;
    }

    DraconicEvolutionBlocks(String unlocalizedName, String localizedName) {
        this(unlocalizedName, localizedName, 0);
    }

    DraconicEvolutionBlocks(String unlocalizedName, int meta) {
        this(unlocalizedName, unlocalizedName, meta);
    }

    DraconicEvolutionBlocks(String unlocalizedName) {
        this(unlocalizedName, unlocalizedName, 0);
    }

    private ModBlock getBlock() {
        if (cachedBlock == null) {
            cachedBlock = new ModBlock(Mods.DraconicEvolution, unlocalizedName, localizedName, meta);
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
