package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;
import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;

import gregtech.api.enums.Mods;

public class DraconicEvolutionBlocks extends ModHandler {

    public final ModBlock DissEnchanter;

    public DraconicEvolutionBlocks() {
        super(Mods.DraconicEvolution);

        DissEnchanter = new ModBlock(mod, "dissEnchanter", 0, "Disenchanter");
    }
}
