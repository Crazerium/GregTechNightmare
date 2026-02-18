package com.EvgenWarGold.GregTechNightmare.Utils;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;
import net.minecraft.item.ItemStack;

public class GTN_RecipeUtils {

    public static Object getCircuit(Materials circuitTier) {
        return OrePrefixes.circuit.get(circuitTier);
    }
}
