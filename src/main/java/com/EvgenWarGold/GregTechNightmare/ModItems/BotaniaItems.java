package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;
import gregtech.api.enums.Mods;

public class BotaniaItems extends ModHandler {

    public final ModItem TerraShatterer;

    public BotaniaItems() {
        super(Mods.Botania);

        TerraShatterer = new ModItem(mod, "terraPick", 0, "Terra Shatterer");
    }
}
