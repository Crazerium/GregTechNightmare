package com.EvgenWarGold.GregTechNightmare.ModItems;

import com.EvgenWarGold.GregTechNightmare.Api.ModHandler;
import com.EvgenWarGold.GregTechNightmare.Api.ModItem;

import gregtech.api.enums.Mods;

public class BloodMagicItems extends ModHandler {

    public final ModItem BoundPickaxe;
    public final ModItem LifeShard;
    public final ModItem SoulShard;
    public final ModItem ReinforcedSlate;
    public final ModItem Incendium;
    public final ModItem Offensa;

    public BloodMagicItems() {
        super(Mods.BloodMagic);

        BoundPickaxe = createItem("boundPickaxe", "Bound Pickaxe");
        LifeShard = createItem("bloodMagicBaseItems", "Life Shard", 28);
        SoulShard = createItem("bloodMagicBaseItems", "Soul Shard", 29);
        ReinforcedSlate = createItem("reinforcedSlate", "Reinforced Slate");
        Incendium = createItem("incendium", "Incendium");
        Offensa = createItem("bloodMagicBaseAlchemyItems", "Offensa");
    }
}
