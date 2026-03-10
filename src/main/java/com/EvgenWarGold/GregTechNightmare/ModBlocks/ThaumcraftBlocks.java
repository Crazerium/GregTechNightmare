package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;
import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import gregtech.api.enums.Mods;

public class ThaumcraftBlocks extends ModHandler {

    public final ModBlock ArcaneBore;

    public ThaumcraftBlocks() {
        super(Mods.Thaumcraft);

        ArcaneBore = new ModBlock(mod, "blockWoodenDevice", 5, "Arcane Bore");
    }
}
