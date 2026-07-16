package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;
import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public class EnderIOBlocks extends ModHandler {

    public final ModBlock SliceNSplice;

    public EnderIOBlocks() {
        super(Mods.EnderIO);

        SliceNSplice = createBlock("blockSliceAndSplice");
    }
}
