package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public class BotaniaItems extends ModHandler {

    public final ModItem TerraShatterer;
    public final ModItem ManaLensBore;
    public final ModItem ManaSteelIngot;
    public final ModItem RuneOfEarth;
    public final ModItem TerrasteelIngot;

    public BotaniaItems() {
        super(Mods.Botania);

        TerraShatterer = createItem("terraPick", "Terra Shatterer");
        ManaLensBore = createItem("lens", "Mana Lens: Bore", 7);
        ManaSteelIngot = createItem("manaResource", "Manasteel ingot");
        RuneOfEarth = createItem("rune", "Rune Of Earth", 2);
        TerrasteelIngot = createItem("manaResource", "Terrasteel ingot", 4);
    }
}
