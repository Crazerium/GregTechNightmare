package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import static com.EvgenWarGold.GregTechNightmare.ModBlocks.ModBlocks.getBlock;
import static gregtech.api.enums.Mods.Chisel;

import net.minecraft.block.Block;

import it.unimi.dsi.fastutil.Pair;

public class ChiselBlocks {

    public static Pair<Block, Integer> OAK_WOOD_PLANKS_10;

    public static void init() {
        if (Chisel.isModLoaded()) {
            OAK_WOOD_PLANKS_10 = Pair.of(getBlock(Chisel, "oak_planks"), 10);
        }
    }
}
