package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public class EnderIOBlocks extends ModHandler {

    public final ModItem SliceNSplice;

    public EnderIOBlocks() {

        super(Mods.EnderIO);

        SliceNSplice = new ModItem(mod, "blockSliceAndSplice", 0, "Slice'N'Splice");
    }
}
