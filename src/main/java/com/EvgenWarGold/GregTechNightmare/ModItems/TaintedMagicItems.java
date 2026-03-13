package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public class TaintedMagicItems extends ModHandler {

    public final ModItem WandFocusTime;

    public TaintedMagicItems() {
        super(Mods.TaintedMagic);

        WandFocusTime = new ModItem(mod, "ItemFocusTime", 0, "Wand Focus: Time");
    }
}
