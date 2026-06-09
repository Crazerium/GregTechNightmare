package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;
import gregtech.api.enums.Mods;

public class AE2FluidCraftItems extends ModHandler {

    public final ModItem SuperStockReplenisher;

    public AE2FluidCraftItems() {
        super(Mods.AE2FluidCraft);

        SuperStockReplenisher = new ModItem(mod, "super_stock_replenisher", 0, "Super Stock Replenisher");
    }
}
