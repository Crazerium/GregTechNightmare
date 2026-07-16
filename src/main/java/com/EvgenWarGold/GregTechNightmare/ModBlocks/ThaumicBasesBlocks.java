package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;
import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;

import gregtech.api.enums.Mods;

public class ThaumicBasesBlocks extends ModHandler {

    public final ModBlock Overchanter;
    public final ModBlock VoidBlock;
    public final ModBlock EarthCrystalBlock;
    public final ModBlock FireCrystalBlock;
    public final ModBlock AirCrystalBlock;
    public final ModBlock WaterCrystalBlock;
    public final ModBlock OrderCrystalBlock;
    public final ModBlock EntropyCrystalBlock;

    public ThaumicBasesBlocks() {
        super(Mods.ThaumicBases);

        Overchanter = createBlock("overchanter");
        VoidBlock = createBlock("voidBlock");
        EarthCrystalBlock = createBlock("crystalBlock", 3);
        FireCrystalBlock = createBlock("crystalBlock", 1);
        AirCrystalBlock = createBlock("crystalBlock");
        WaterCrystalBlock = createBlock("crystalBlock", 2);
        OrderCrystalBlock = createBlock("crystalBlock", 4);
        EntropyCrystalBlock = createBlock("crystalBlock", 5);
    }
}
