package com.EvgenWarGold.GregTechNightmare.ModItems;

import net.minecraft.item.ItemStack;

import gregtech.api.enums.Mods;

public enum BloodMagicItems {

    // spotless:off
    BoundPickaxe("boundPickaxe", "Bound Pickaxe"),
    LifeShard("bloodMagicBaseItems", "Life Shard", 28),
    SoulShard("bloodMagicBaseItems", "Soul Shard", 29),
    ReinforcedSlate("reinforcedSlate", "Reinforced Slate"),
    Incendium("incendium", "Incendium"),
    Offensa("bloodMagicBaseAlchemyItems", "Offensa"),
    ;
    //spotless:on

    private final String unlocalizedName;
    private final String localizedName;
    private final int meta;
    private ModItem cachedItem;

    BloodMagicItems(String unlocalizedName, String localizedName, int meta) {
        this.unlocalizedName = unlocalizedName;
        this.localizedName = localizedName;
        this.meta = meta;
    }

    BloodMagicItems(String unlocalizedName, String localizedName) {
        this(unlocalizedName, localizedName, 0);
    }

    private ModItem getItem() {
        if (cachedItem == null) {
            cachedItem = new ModItem(Mods.BloodMagic, unlocalizedName, localizedName, meta);
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
