package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;
import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;

import gregtech.api.enums.Mods;

public class ThaumicBasesBlocks extends ModHandler {

    public final ModBlock Overchanter;
    public final ModBlock VoidBlock;

    public ThaumicBasesBlocks() {
        super(Mods.ThaumicBases);

        Overchanter = new ModBlock(mod, "overchanter", 0, "Overchanter");
        VoidBlock = new ModBlock(mod, "voidBlock", 0, "Void Block");
    }
}
