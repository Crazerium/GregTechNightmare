package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;
import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;

public class BloodMagicBlocks extends ModHandler {

    public final ModBlock RitualStone;

    public BloodMagicBlocks() {
        super("AWWayofTime");

        RitualStone = new ModBlock(mod, "ritualStone", 0, "Ritual Stone");
    }
}
