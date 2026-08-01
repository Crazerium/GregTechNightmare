package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;
import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;

import gregtech.api.enums.Mods;

public class BotaniaBlocks extends ModHandler {

    public final ModBlock LivingRock;
    public final ModBlock ManaPool;
    public final ModBlock AlfGlass;

    public BotaniaBlocks() {
        super(Mods.Botania);

        LivingRock = createBlock("livingrock");
        ManaPool = createBlock("pool");
        AlfGlass = createBlock("elfGlass");
    }
}
