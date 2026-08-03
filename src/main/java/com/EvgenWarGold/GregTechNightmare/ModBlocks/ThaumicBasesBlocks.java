package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;

import gregtech.api.enums.Mods;

public enum ThaumicBasesBlocks {

    // spotless:off
    Overchanter("overchanter"),
    VoidBlock("voidBlock"),
    EarthCrystalBlock("crystalBlock", 3),
    FireCrystalBlock("crystalBlock", 1),
    AirCrystalBlock("crystalBlock"),
    WaterCrystalBlock("crystalBlock", 2),
    OrderCrystalBlock("crystalBlock", 4),
    EntropyCrystalBlock("crystalBlock", 5),
    SalisMundusBlock("blockSalisMundus"),
    ;
    //spotless:on

    private final String unlocalizedName;
    private final String localizedName;
    private final int meta;
    private ModBlock cachedBlock;

    ThaumicBasesBlocks(String unlocalizedName, String localizedName, int meta) {
        this.unlocalizedName = unlocalizedName;
        this.localizedName = localizedName;
        this.meta = meta;
    }

    ThaumicBasesBlocks(String unlocalizedName, String localizedName) {
        this(unlocalizedName, localizedName, 0);
    }

    ThaumicBasesBlocks(String unlocalizedName, int meta) {
        this(unlocalizedName, unlocalizedName, meta);
    }

    ThaumicBasesBlocks(String unlocalizedName) {
        this(unlocalizedName, unlocalizedName, 0);
    }

    private ModBlock getBlock() {
        if (cachedBlock == null) {
            cachedBlock = new ModBlock(Mods.ThaumicBases, unlocalizedName, localizedName, meta);
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
