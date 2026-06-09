package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;
import gregtech.api.enums.Mods;

public class AppliedEnergisticsItems extends ModHandler {

    public final ModItem MEController;
    public final ModItem AccelerationCard;
    public final ModItem CapacityCard;

    public AppliedEnergisticsItems() {
        super(Mods.AppliedEnergistics2);

        MEController = new ModItem(mod, "tile.BlockController", 0, "ME Controller");
        AccelerationCard = new ModItem(mod, "item.ItemMultiMaterial", 30, "Acceleration Card");
        CapacityCard = new ModItem(mod, "item.ItemMultiMaterial", 27, "Capacity Card");
    }
}
