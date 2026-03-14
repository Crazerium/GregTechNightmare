package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;
import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;

import gregtech.api.enums.Mods;

public class BloodMagicBlocks extends ModHandler {

    public final ModBlock RitualStone;

    public BloodMagicBlocks() {
        super(Mods.BloodMagic);

        RitualStone = new ModBlock(mod, "ritualStone", 0, "Ritual Stone");
    }
}
