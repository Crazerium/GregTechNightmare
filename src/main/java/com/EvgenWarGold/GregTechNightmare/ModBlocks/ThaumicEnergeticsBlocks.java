package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;
import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import gregtech.api.enums.Mods;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.block.Block;

import static gregtech.api.enums.Mods.ThaumicEnergistics;

public class ThaumicEnergeticsBlocks extends ModHandler {

    public final ModBlock ARCANE_ASSEMBLER;

    public ThaumicEnergeticsBlocks() {
        super(ThaumicEnergistics);

        ARCANE_ASSEMBLER = new ModBlock(mod, "thaumicenergistics.block.arcane.assembler", 0, "Arcane Assembler");
    }
}
