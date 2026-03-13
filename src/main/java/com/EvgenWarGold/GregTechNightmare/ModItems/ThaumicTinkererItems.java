package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public class ThaumicTinkererItems extends ModHandler {

    public final ModItem WandFocusEfreetFlame;

    public ThaumicTinkererItems() {
        super(Mods.ThaumicTinkerer);

        WandFocusEfreetFlame = new ModItem(mod, "focusSmelt", 0, "Wand Focus: Efreet's Flame");
    }
}
