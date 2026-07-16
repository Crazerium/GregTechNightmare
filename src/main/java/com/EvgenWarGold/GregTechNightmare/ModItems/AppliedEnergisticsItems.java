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

        MEController = createItem("tile.BlockController", "ME Controller");
        AccelerationCard = createItem("item.ItemMultiMaterial", "Acceleration Card", 30);
        CapacityCard = createItem("item.ItemMultiMaterial", "Capacity Card", 27);
    }
}
