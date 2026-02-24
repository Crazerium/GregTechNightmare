package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import static gregtech.api.enums.Mods.ThaumicEnergistics;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;
import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;

public class ThaumicEnergeticsBlocks extends ModHandler {

    public final ModBlock ARCANE_ASSEMBLER;

    public ThaumicEnergeticsBlocks() {
        super(ThaumicEnergistics);

        ARCANE_ASSEMBLER = new ModBlock(mod, "thaumicenergistics.block.arcane.assembler", 0, "Arcane Assembler");
    }
}
