package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public class ThaumcraftItems extends ModHandler {

    public final ModItem salisMundus;
    public final ModItem PickaxeElemental;

    public ThaumcraftItems() {
        super(Mods.Thaumcraft);

        salisMundus = createItem("ItemResource", "Salis Mundus", 14);
        PickaxeElemental = createItem("ItemPickaxeElemental", "Pickaxe Elemental");
    }
}
