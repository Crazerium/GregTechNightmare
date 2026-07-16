package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public class BotaniaItems extends ModHandler {

    public final ModItem TerraShatterer;
    public final ModItem ManaLensBore;

    public BotaniaItems() {
        super(Mods.Botania);

        TerraShatterer = createItem("terraPick", "Terra Shatterer");
        ManaLensBore = createItem("lens", "Mana Lens: Bore", 7);
    }
}
