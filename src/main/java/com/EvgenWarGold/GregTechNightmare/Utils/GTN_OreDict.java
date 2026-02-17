package com.EvgenWarGold.GregTechNightmare.Utils;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;
import net.minecraft.item.ItemStack;

public class GTN_OreDict {

    public static ItemStack getIngot(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.ingot, materials, amount);
    }

    public static ItemStack getIngot(Materials materials) {
        return GTOreDictUnificator.get(OrePrefixes.ingot, materials, 1L);
    }

    public static ItemStack getDust(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.dust, materials, amount);
    }

    public static ItemStack getDust(Materials materials) {
        return GTOreDictUnificator.get(OrePrefixes.dust, materials, 1L);
    }

    public static ItemStack getGem(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.gem, materials, amount);
    }

    public static ItemStack getGem(Materials materials) {
        return GTOreDictUnificator.get(OrePrefixes.gem, materials, 1L);
    }

    public static ItemStack getBlock(Materials materials, long amount) {
        return GTOreDictUnificator.get(OrePrefixes.block, materials, amount);
    }

    public static ItemStack getBlock(Materials materials) {
        return GTOreDictUnificator.get(OrePrefixes.block, materials, 1L);
    }
}
