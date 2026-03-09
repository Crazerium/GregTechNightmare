package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public class BloodMagicItems extends ModHandler {

    public final ModItem BoundPickaxe;

    public BloodMagicItems() {
        super(Mods.BloodMagic);

        BoundPickaxe = new ModItem(mod, "boundPickaxe", 0, "Bound Pickaxe");
    }
}
