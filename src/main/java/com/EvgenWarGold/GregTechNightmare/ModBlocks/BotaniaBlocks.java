package com.EvgenWarGold.GregTechNightmare.ModBlocks;

import com.EvgenWarGold.GregTechNightmare.Api.ModBlock;
import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;

import gregtech.api.enums.Mods;

public class BotaniaBlocks extends ModHandler {

    public final ModBlock LivingRock;
    public final ModBlock ManaPool;

    public BotaniaBlocks() {
        super(Mods.Botania);

        LivingRock = new ModBlock(mod, "livingrock", 0, "Livingrock");
        ManaPool = new ModBlock(mod, "pool", 0, "Mana Pool");
    }
}
