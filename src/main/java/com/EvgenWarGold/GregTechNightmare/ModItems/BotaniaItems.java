package com.EvgenWarGold.GregTechNightmare.ModItems;

import gregtech.api.enums.Mods;
import net.minecraft.item.ItemStack;

public enum BotaniaItems {
    // spotless:off
    TerraShatterer("terraPick", "Terra Shatterer"),
    ManaLensBore("lens", "Mana Lens: Bore", 7),
    ManaSteelIngot("manaResource", "Manasteel ingot"),
    RuneOfEarth("rune", "Rune Of Earth", 2),
    TerrasteelIngot("manaResource", "Terrasteel ingot", 4),
    ;
    //spotless:on

    private final String unlocalizedName;
    private final String localizedName;
    private final int meta;
    private ModItem cachedItem;

    BotaniaItems(String unlocalizedName, String localizedName, int meta) {
        this.unlocalizedName = unlocalizedName;
        this.localizedName = localizedName;
        this.meta = meta;
    }

    BotaniaItems(String unlocalizedName, String localizedName) {
        this(unlocalizedName, localizedName, 0);
    }

    private ModItem getItem() {
        if (cachedItem == null) {
            cachedItem = new ModItem(Mods.Botania, unlocalizedName, localizedName, meta);
        }
        return cachedItem;
    }

    public ItemStack get() {
        return getItem().get();
    }

    public ItemStack get(int count) {
        return getItem().get(count);
    }
}
