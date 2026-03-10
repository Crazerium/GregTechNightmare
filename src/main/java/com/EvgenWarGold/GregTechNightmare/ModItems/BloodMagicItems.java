package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public class BloodMagicItems extends ModHandler {

    public final ModItem BoundPickaxe;
    public final ModItem LifeShard;
    public final ModItem SoulShard;

    public BloodMagicItems() {
        super(Mods.BloodMagic);

        BoundPickaxe = new ModItem(mod, "boundPickaxe", 0, "Bound Pickaxe");
        LifeShard = new ModItem(mod, "bloodMagicBaseItems", 28, "Life Shard");
        SoulShard = new ModItem(mod, "bloodMagicBaseItems", 29, "Soul Shard");
    }
}
