package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;

import gregtech.api.enums.Mods;

public enum BotaniaBlocks {

    // spotless:off
    LivingRock("livingrock"),
    ManaPool("pool"),
    AlfGlass("elfGlass"),
        ;
    //spotless:on

    private final String unlocalizedName;
    private final String localizedName;
    private final int meta;
    private ModBlock cachedBlock;

    BotaniaBlocks(String unlocalizedName, String localizedName, int meta) {
        this.unlocalizedName = unlocalizedName;
        this.localizedName = localizedName;
        this.meta = meta;
    }

    BotaniaBlocks(String unlocalizedName, String localizedName) {
        this(unlocalizedName, localizedName, 0);
    }

    BotaniaBlocks(String unlocalizedName, int meta) {
        this(unlocalizedName, unlocalizedName, meta);
    }

    BotaniaBlocks(String unlocalizedName) {
        this(unlocalizedName, unlocalizedName, 0);
    }

    private ModBlock getBlock() {
        if (cachedBlock == null) {
            cachedBlock = new ModBlock(Mods.Botania, unlocalizedName, localizedName, meta);
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
