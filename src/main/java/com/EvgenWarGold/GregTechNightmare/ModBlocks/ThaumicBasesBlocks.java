package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;
import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;

import gregtech.api.enums.Mods;

public class ThaumicBasesBlocks extends ModHandler {

    public final ModBlock Overchanter;
    public final ModBlock VoidBlock;
    public final ModBlock EarthCrystalBlock;

    public ThaumicBasesBlocks() {
        super(Mods.ThaumicBases);

        Overchanter = createBlock("overchanter");
        VoidBlock = createBlock("voidBlock");
        EarthCrystalBlock = createBlock("crystalBlock", 3);
    }
}
