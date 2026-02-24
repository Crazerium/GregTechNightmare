package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import net.minecraft.block.Block;

import com.EvgenWarGold.GregTechNightmare.GregTech.GTN_ItemList;

import gregtech.api.enums.Mods;

public class ModBlocks {

    public static final Block FALLBACK_ERROR = GTN_ItemList.TestCasing.getBlock();

    public static void init() {
        ChiselBlocks.init();
    }

    public static Block getBlock(Mods mod, String name) {
        Block block = Block.getBlockFromName(mod.ID + ":" + name);

        if (block == null) {
            block = FALLBACK_ERROR;
        }

        return block;
    }
}
