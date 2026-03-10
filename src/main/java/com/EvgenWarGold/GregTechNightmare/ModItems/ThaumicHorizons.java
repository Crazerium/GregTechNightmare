package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public class ThaumicHorizons extends ModHandler {

    public final ModItem WandFocusDisintegration;

    public ThaumicHorizons() {
        super(Mods.ThaumicHorizons);

        WandFocusDisintegration = new ModItem(mod, "focusDisintegration", 0, "Wand Focus: Disintegration");
    }
}
